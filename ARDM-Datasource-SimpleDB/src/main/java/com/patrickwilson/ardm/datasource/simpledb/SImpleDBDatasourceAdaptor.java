package com.patrickwilson.ardm.datasource.simpledb;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.patrickwilson.ardm.api.annotation.Entity;
import com.patrickwilson.ardm.api.key.EntityKey;
import com.patrickwilson.ardm.api.key.SimpleEnitityKey;
import com.patrickwilson.ardm.datasource.api.CRUDDatasourceAdaptor;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryEntityException;
import com.patrickwilson.ardm.datasource.api.exception.RepositoryInteractionException;

/**
 * Created by pwilson on 5/5/15.
 */
public class SimpleDBDatasourceAdaptor implements CRUDDatasourceAdaptor {

    private static final Pattern COLUMN_NAMING_PATTERN = Pattern.compile("_P\\:(.+?)\\:(.*)");
    private static final String ENTITY_PROP_PREFIX = "_P:";
    private Gson gson = new GsonBuilder().create();
    private final AmazonSimpleDBAsync simpleDbClient;

    public SimpleDBDatasourceAdaptor(AmazonSimpleDBAsync simpleDbClient) {
        this.simpleDbClient = simpleDbClient;
    }

    @Override
    public <ENTITY> ENTITY save(ENTITY entity, Class<ENTITY> clazz) {
        String domain = getDomainOrTable(clazz);
        PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(clazz);

        PutAttributesRequest req = new PutAttributesRequest();
        List<ReplaceableAttribute> attributes = new LinkedList<>();

        req.setDomainName(domain);

        String entityId = UUID.randomUUID().toString();
        String entityKeyPropName = null;
        for (PropertyDescriptor prop: properties) {

            if (isEntityKeyProperty(prop)) {
                entityKeyPropName = prop.getName();
                EntityKey key = getEntityKey(prop, entity);
                if (key != null) {
                    if (String.class.isAssignableFrom(key.getKeyClass())) {
                        entityId = (String) key.getKey();
                    }
                    throw new RepositoryEntityException("Invalid Key Type.  String keys are required. Entity Type: " + clazz.getName());
                }

                continue; //don't write the ID to the attributes list.
            }

            String fieldKey = ENTITY_PROP_PREFIX + prop.getPropertyType().getName() + ":" + prop.getName();

            String strValue;
            try {
                Object value = PropertyUtils.getProperty(entity, prop.getName());
                strValue = (value != null) ? value.toString() : "_NULL_";

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                //TODO I am ignoring these properties for now.  Better way to handle them?
                continue;
            }

            attributes.add(new ReplaceableAttribute().withReplace(true).withName(fieldKey).withValue(strValue));
        }

        req.setItemName(entityId);
        req.setAttributes(attributes);

        try {
            long startPut = System.currentTimeMillis();
            simpleDbClient.putAttributes(req);
            System.out.println("Simple DB Put time: " + (System.currentTimeMillis() - startPut));
        } catch (Throwable throwable) {
            //this is an unexpected datasource problem...
            throw new RepositoryInteractionException("Error communicating with Amazon Simple DB.", throwable);
        }

        EntityKey<String> key = new SimpleEnitityKey<>(entityId, String.class);


        if (entityKeyPropName == null) {
            throw new RepositoryEntityException("The entity [" + clazz + "] does not have a setter for its primary key");
        }

        if (!setProperty(entity, entityKeyPropName, EntityKey.class, key)) {
            //TODO = did not set the entity key, what makes sense to do here?
        }

        return entity;
    }

    @Override
    public <ENTITY> void delete(ENTITY entity, Class<ENTITY> clazz) {

    }

    @Override
    public <ENTITY> ENTITY findOne(EntityKey id, Class<ENTITY> clazz) {
        String domain = getDomainOrTable(clazz);
        PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(clazz);

        GetAttributesRequest req = new GetAttributesRequest();
        List<String> attributeNames = new LinkedList<>();

        req.setDomainName(domain);

        if (!String.class.isAssignableFrom(id.getKeyClass())) {
            //only String keys are allowed here.
            throw new RepositoryEntityException("Invalid Key Type - only String type is allowed for Amazon SimpleDB.  Entity type: [" + clazz + "]");
        }

        String entityId = (String) id.getKey();

        req.setItemName(entityId);
        req.setConsistentRead(true);

        GetAttributesResult results;
        try {
            long startEnd = System.currentTimeMillis();
            results = simpleDbClient.getAttributes(req);
            System.out.println("Simple DB Get time: " + (System.currentTimeMillis() - startEnd));
        } catch (Throwable throwable) {
            //this is an unexpected datasource problem...
            throw new RepositoryInteractionException("Error communicating with Amazon Simple DB.", throwable);
        }
        Object entity;
        try {
            entity = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RepositoryEntityException("Unable to construct entity of type: " + clazz);
        }

        for (Attribute attr: results.getAttributes()) {
            String tablePropName = attr.getName();
            //remove the prefix.
            Matcher columnNameMatcher = COLUMN_NAMING_PATTERN.matcher(tablePropName);
            if (columnNameMatcher.find()) {
                String type = columnNameMatcher.group(1);
                String propName = columnNameMatcher.group(2);

                Class paramType;
                try {
                    paramType = loadClassForType(type);
                } catch (ClassNotFoundException e) {
                    //this type is no longer on the classpath.  What to do here?
                    //ignore.
                    //TODO Does it make sense to be lean here?
                    continue;
                }

                String strValue = attr.getValue(); //try to cast this to a native type.


                Object value;
                try {
                    value = parseObject(strValue, paramType);
                } catch (Exception e) {
                    throw new RepositoryEntityException("Type mismatch!  It looks like the serialized string cannot be parsed into expected type: " + paramType + ", str value: [" + strValue + "]");
                }

                if (!setProperty(entity, propName, paramType, value)) {
                    //TODO eventually we might care about this (right now assume the entity does not include this property in the class anymore...
                    int i = 0;
                }

            } else {
                //the column in the db is not formatted correctly.  Ignore this field.
                //TODO - is the above decision sane?
                continue;
            }

        }

        String entityKeyPropName = getEntityKeyPropName(clazz);
        if (entityKeyPropName == null) {
            throw new RepositoryEntityException("The entity [" + clazz + "] does not have a setter for its primary key");
        }

        if (!setProperty(entity, entityKeyPropName, EntityKey.class, new SimpleEnitityKey<>(entityId, String.class))) {
            //TODO = did not set the entity key, what makes sense to do here?
        }

        return (ENTITY) entity;


    }

    //TODO this should be a common utility

    private static boolean isEntityKeyProperty(PropertyDescriptor propertyDescriptor) {
        return EntityKey.class.isAssignableFrom(propertyDescriptor.getPropertyType());
    }

    private static String getEntityKeyPropName(Class clazz) {
        PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(clazz);

        for(PropertyDescriptor prop: properties) {
            if (isEntityKeyProperty(prop)) {
               return prop.getName();

            }
        }
        return null;
    }

    private static EntityKey getEntityKey(PropertyDescriptor propertyDescriptor, Object entity) {
        if (isEntityKeyProperty(propertyDescriptor)) {
            String name = propertyDescriptor.getName();
            try {
                Object propValue = PropertyUtils.getProperty(entity, name);
                if (propValue != null)
                    return (EntityKey) propValue;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RepositoryEntityException(entity.getClass(), e);
            }
        }
        return null;
    }

    private String getDomainOrTable(Class<?> clazz) {
        Entity entityAnnotation = clazz.getAnnotation(Entity.class);
        if (entityAnnotation != null) {
            String domain = entityAnnotation.domainOrTable();
            if (!Entity.NO_DOMAIN_OR_TABLE.equals(domain)) {
                return domain;
            }
        }
        return null;
    }



    /**
    This is needed because PropertyUtils assumes Setter methods are void which is nonsensical...

         @return whether the property was actually updated.
     */
    private boolean setProperty(Object entity, String propName, Class propType, Object value) {

        try {

            char firstChar = propName.charAt(0);
            String withOutFirst = propName.substring(1);
            String camelCasedName = Character.toUpperCase(firstChar) + withOutFirst;
            String methodName = "set" + camelCasedName;

            //TODO - this seems slow...  Is there a better way?
            for (Method method: entity.getClass().getMethods()) {
                if (methodName.equals(method.getName())) {
                    method.invoke(entity, value);
                    return true;
                }
            }
            return true;

        } catch (InvocationTargetException | IllegalAccessException e) {
            return false;
        }
    }

    private static Map<Class, Function<String, Object>> fromStringFactoryBindings = ImmutableMap
            .<Class, Function<String, Object>>builder()
            .put(Long.class, input -> {
                return Long.parseLong(input);
            })
            .put(Integer.class, input -> {
                return Integer.parseInt(input);
            })
            .put(Character.class, input -> {
                if (input.length() != 1) {
                    //TODO should probably also have a better strategy to surface this situation.  Suggests a non backwards compatible change.
                    return null;
                }
                return input.charAt(0);
            })
            .put(Short.class, input -> {
                return Short.parseShort(input);
            })
            .put(Boolean.class, input -> {
                return Boolean.parseBoolean(input);
            })
            .put(Double.class, input -> {
                return Double.parseDouble(input);
            })
            .put(Float.class, input -> {
                return Float.parseFloat(input);
            })
            .put(String.class, input -> {
                return input;
            })
            .put(Date.class, input -> {
                return new Date(Long.parseLong(input));
            })
            .put(EntityKey.class, input -> {
                return new SimpleEnitityKey<String>(input, String.class);
            })
            .build();

    private static Map<Class, Function<Object, String>> toStringFactoryBindings = ImmutableMap
            .<Class, Function<Object, String>>builder()
            .put(Long.class, input -> input.toString())
            .put(Integer.class, input -> input.toString())
            .put(Character.class, input -> input.toString())
            .put(Short.class, input -> input.toString())
            .put(Boolean.class, input -> input.toString())
            .put(Double.class, input -> input.toString())
            .put(Float.class, input -> input.toString())
            .put(String.class, input -> input.toString())
            .put(Date.class, input -> {
                long epochTime = ((Date)input).getTime();
                return Long.toString(epochTime);
            })
            .build();

    private Object parseObject(String raw, Class type) {
        if (fromStringFactoryBindings.containsKey(type)) {
            return fromStringFactoryBindings.get(type).apply(raw);
        }
        else {
            //this is a complex sub object.  eventually use GSON for this parsing.
            return null;
        }
    }

    private String serializeObject(Object o, Class type) {
        if (toStringFactoryBindings.containsKey(type)) {
            return toStringFactoryBindings.get(type).apply(o);
        } else {
            //this is going to be a complex sub object - need to use GSON to serialize.
            return "";
        }
    }

    private static Map<String, Class> classLoadingFactory = ImmutableMap
            .<String, Class>builder()
            .put("long", Long.class)
            .put("int", Integer.class)
            .put("char", Character.class)
            .put("short", Short.class)
            .put("boolean", Boolean.class)
            .put("double", Double.class)
            .put("float", Float.class)
            .build();

    private Class loadClassForType(String className) throws ClassNotFoundException {
        if (classLoadingFactory.containsKey(className)) {
            //this is a primitive.
            return classLoadingFactory.get(className);
        }

        return Class.forName(className);

    }
}
