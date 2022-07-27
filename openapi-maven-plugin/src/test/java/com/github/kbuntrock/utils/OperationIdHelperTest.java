package com.github.kbuntrock.utils;

import com.github.kbuntrock.configuration.CommonApiConfiguration;
import com.github.kbuntrock.configuration.OperationIdHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Kevin Buntrock
 */
public class OperationIdHelperTest {

    @Test
    public void config_parsing() {

        String config1 = CommonApiConfiguration.DEFAULT_OPERATION_ID;
        OperationIdHelper helper = new OperationIdHelper(config1);
        String operationId1 = helper.toOperationId("UserController", "User", "findUsers");
        Assertions.assertEquals("UserController.findUsers", operationId1);

        String config2 = "operation_{tag_name}{class_name}_{method_name}_end";
        OperationIdHelper helper2 = new OperationIdHelper(config2);
        String operationId2 = helper2.toOperationId("UserController", "User", "findUsers");
        Assertions.assertEquals("operation_UserUserController_findUsers_end", operationId2);

        String config3 = "{method_name}";
        OperationIdHelper helper3 = new OperationIdHelper(config3);
        String operationId3 = helper3.toOperationId("UserController", "User", "findUsers");
        Assertions.assertEquals("findUsers", operationId3);

        String config4 = "{tag_name}{method_name}";
        OperationIdHelper helper4 = new OperationIdHelper(config4);
        String operationId4 = helper4.toOperationId("UserController", "User", "findUsers");
        Assertions.assertEquals("UserfindUsers", operationId4);
    }
}
