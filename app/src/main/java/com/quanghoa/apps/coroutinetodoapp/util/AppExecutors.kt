package com.quanghoa.apps.coroutinetodoapp.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.coroutines.CoroutineContext

const val THREAD_COUNT = 3

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation(e.g. disk reads don't wait behind
 * webservice requests).
 */
open class AppExecutors constructor(
    val ioContext: CoroutineContext = Dispatchers.Default,
    val networkContext: CoroutineContext = newFixedThreadPoolContext(THREAD_COUNT, "networkIO"),
    val uiContext: CoroutineContext = Dispatchers.Main
)