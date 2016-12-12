package com.patrickwilson.ardm.samples.simpledb;

import java.util.List;
import com.patrickwilson.ardm.api.annotation.Query;
import com.patrickwilson.ardm.api.annotation.Repository;
import com.patrickwilson.ardm.api.repository.CRUDRepository;

/**
 * Created by pwilson on 5/5/15.
 */
@Repository(User.class)
public interface UserRepository extends CRUDRepository<User, String> {


    @Query
    List<User> findByFirstName(String fname);


}
