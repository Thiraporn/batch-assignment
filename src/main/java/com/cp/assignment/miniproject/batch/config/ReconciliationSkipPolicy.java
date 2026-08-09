package com.cp.assignment.miniproject.batch.config;

import jakarta.validation.ValidationException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeParseException;

@Component
public class ReconciliationSkipPolicy implements SkipPolicy {
    //เพราะ  application ไม่ปล่อยให้ข้อมูลเสียจำนวนมากแล้วทำงานต่อไปเรื่อย ๆ
    //ถ้า input มีหลายล้าน rows แล้วเสีย 500,000 rows แต่ระบบยังทำต่อจนจบ อาจเป็นสัญญาณว่า input file มีปัญหารุนแรง
    /*
     * ป้องกันไม่ให้ข้อมูลเสียจำนวนมากแล้ว Batch
     * ยังทำงานต่อไปเรื่อย ๆ
     *
     * เช่น input มี 1,000,000 rows
     * แต่เสีย 500,000 rows
     * อาจแสดงว่า input file มีปัญหารุนแรง
     */
    private static final long SKIP_LIMIT = 100;

    @Override
    public boolean shouldSkip(  Throwable t,    long skipCount
    ) {

        // Maximum number of records that can be skipped
        if (skipCount >= SKIP_LIMIT) {
            return false;
        }


        // =========================
        // Data errors → SKIP
        // Data errors → skip and write to Error_Records.csv
        // =========================

        if (t instanceof DateTimeParseException) {// Invalid date
            return true;
        }

        if (t instanceof NumberFormatException) {// Invalid number
            return true;
        }

//        if (t instanceof IllegalArgumentException) {//Validation/data error ถ้าคุณเป็นคน throw
//            return true;
//        }

        if (t instanceof ValidationException) {//Validation/data error ถ้าคุณเป็นคน throw
            return true;
        }
        // =========================
        // System / programming errors
        // → DO NOT SKIP
        // =========================
       /* if (t instanceof SQLException) {//Database/system failure → batch should fail
            return false;
        }

        if (t instanceof IOException) {//File/system failure → batch should fail
            return false;
        }

        if (t instanceof NullPointerException) {//Programming bug → batch should fail
            return false;
        }*/

        // Unknown exception → fail
        return false;
    }
}