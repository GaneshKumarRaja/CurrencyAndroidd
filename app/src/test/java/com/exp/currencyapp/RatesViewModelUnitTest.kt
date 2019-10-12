package com.exp.currencyapp

import com.exp.currencyapp.model.RateResponseModel
import com.exp.currencyapp.view_model.RatestViewModel
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.mockito.*
import androidx.arch.core.executor.testing.*

import org.mockito.Mockito.*
import io.reactivex.internal.schedulers.ExecutorScheduler
import com.exp.currencyapp.http.RateService
import io.reactivex.disposables.Disposable
import io.reactivex.Scheduler
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


/**
 * unit test for RatesViewModel
 *
 */
class RatesViewModelUnitTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var rateService: RateService

    @InjectMocks
    var viewModel: RatestViewModel = RatestViewModel()

    lateinit var spyViewModel: RatestViewModel

    private var testSingle: Single<RateResponseModel>? = null


    @Before
    fun initAll() {
        MockitoAnnotations.initMocks(this)
        spyViewModel = spy(viewModel)
    }

    @Test
    fun successResponse() {

        var data = RateResponseModel("", "", null)

        var response: Single<RateResponseModel>? = Single.just(data)

        `when`(rateService.getCountries("EUR")).thenReturn(response)

        spyViewModel.refereshRates()

        verify(spyViewModel, times(1)).handleResponse(data)

        verify(spyViewModel, times(0)).handleError(Throwable())

    }


    @Test
    fun errorResponse() {
        var data = RateResponseModel("", "", null)

        var throwable = Throwable("Invalid URL")

        testSingle = Single.error(throwable)

        `when`(rateService.getCountries("EUR")).thenReturn(testSingle)

        spyViewModel.refereshRates()

        verify(spyViewModel, times(0)).handleResponse(data)

        verify(spyViewModel, times(1)).handleError(throwable)

    }


    @Before
    fun setUpRxSchedulers() {

        val immediate = object : Scheduler() {

            override fun scheduleDirect(run: Runnable?, delay: Long, unit: TimeUnit?): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
            }
        }

        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
    }

}
