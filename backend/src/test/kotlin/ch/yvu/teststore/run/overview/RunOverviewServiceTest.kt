package ch.yvu.teststore.run.overview

import ch.yvu.teststore.integration.ListBackedRepository
import ch.yvu.teststore.integration.result.ListBackedResultRepository
import ch.yvu.teststore.integration.run.ListBackedRunRepository
import ch.yvu.teststore.result.Result
import ch.yvu.teststore.result.ResultRepository
import ch.yvu.teststore.run.Run
import ch.yvu.teststore.run.RunRepository
import ch.yvu.teststore.run.overview.RunOverview.RunResult.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.UUID.randomUUID

class RunOverviewServiceTest {

    companion object {
        val testSuiteId = randomUUID()
        val run = Run(randomUUID(), testSuiteId, "abc123", Date(1))
        val passedResult = Result(run.id, "myTest", 0, true, 0)
        val failedResult = Result(run.id, "myTest2", 0, false, 0)
    }

    lateinit var runRepository: RunRepository
    lateinit var resultRepository: ResultRepository

    lateinit var runOverviewService: RunOverviewService

    @Before fun setUp() {
        runRepository = ListBackedRunRepository(ListBackedRepository())
        resultRepository = ListBackedResultRepository(ListBackedRepository())

        runOverviewService = RunOverviewService(runRepository, resultRepository)
    }

    @Test fun returnsRunOverview() {
        runRepository.save(run)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertTrue(runOverview.isPresent)
        assertEquals(runOverview.get().run, run)
    }

    @Test fun returnsNoRunOverviewIfThereAreNoRuns() {
        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertFalse(runOverview.isPresent)
    }

    @Test fun returnsNoRunOverviewIfOnlyRunsFromOtherTestSuiteArePresent() {
        val otherTestSuitedId = randomUUID()
        val otherRun = Run(randomUUID(), otherTestSuitedId, "def123", Date())
        runRepository.save(otherRun)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertFalse(runOverview.isPresent)
    }

    @Test fun returnsLatestRun() {
        val latestRun = Run(randomUUID(), testSuiteId, "abc124", Date(2))
        runRepository.save(run)
        runRepository.save(latestRun)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().run, latestRun)
    }

    @Test fun returnsCorrectResultIfAllResultsArePassedWithoutRetry() {
        runRepository.save(run)

        resultRepository.save(passedResult)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, PASSED)
    }

    @Test fun returnsCorrectResultIfOneResultIsFailedAndHasNoRetries() {
        runRepository.save(run)
        resultRepository.save(failedResult)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, FAILED)
    }

    @Test fun returnsCorrectResultIfOnlyOneResultIsFailed() {
        runRepository.save(run)
        resultRepository.save(passedResult)
        resultRepository.save(failedResult)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, FAILED)
    }

    @Test fun returnsCorrectResultIfRunHasNoResults() {
        runRepository.save(run)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, UNKNOWN)
    }

    @Test fun doesNotConsiderResultsFromOtherRuns() {
        val otherTestSuiteId = randomUUID()
        val otherRun = Run(randomUUID(), otherTestSuiteId, "def123", Date(1))
        val otherFailedResult = Result(otherRun.id, "myOtherTest", 0, false, 0)
        runRepository.save(run)
        runRepository.save(otherRun)
        resultRepository.save(otherFailedResult)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, UNKNOWN)
    }

    @Test fun returnsCorrectResultIfRetryPassed() {
        val passedRetry = Result(run.id, failedResult.testName, 1, true, 0)
        runRepository.save(run)
        resultRepository.save(failedResult)
        resultRepository.save(passedRetry)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, PASSED_WITH_RETRIES)
    }

    @Test fun returnsCorrectResultIfRunHasFailedRetry() {
        val failedRetry = Result(run.id, failedResult.testName, 1, false, 0)
        runRepository.save(run)
        resultRepository.save(failedResult)
        resultRepository.save(failedRetry)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, FAILED)
    }

    @Test fun returnsCorrectResultIfRunHasOnePassedWithRetriesAndOneFailedTest() {
        val passedRetry = Result(run.id, failedResult.testName, 1, true, 0)
        val failedResult2 = Result(run.id, "myFailedTest2", 0, false, 0)
        val failedRetry = Result(run.id, failedResult2.testName, 1, false, 0)
        runRepository.save(run)
        resultRepository.save(failedResult2)
        resultRepository.save(failedRetry)
        resultRepository.save(failedResult)
        resultRepository.save(passedRetry)

        val runOverview = runOverviewService.getLastRunOverview(testSuiteId)

        assertEquals(runOverview.get().result, FAILED)
    }
}