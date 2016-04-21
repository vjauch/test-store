package ch.yvu.teststore.result

import ch.yvu.teststore.common.Model
import com.datastax.driver.mapping.annotations.PartitionKey
import com.datastax.driver.mapping.annotations.Table
import java.util.*

@Table(name = "result")
data class Result(
        @PartitionKey(0) var run: UUID,
        @PartitionKey(1) var testName: String,
        @PartitionKey(2) var retryNum: Int,
        var passed: Boolean,
        var durationMillis: Long) : Model