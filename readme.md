
TODO:

1 . OutOfMemoryError
2020-08-31 01:25:29.248 pl.mrodkiewicz.imageeditor E/Bitmap: OOM allocating Bitmap with dimensions 7360 x 4912
    
    --------- beginning of crash
2020-08-31 01:25:29.341 pl.mrodkiewicz.imageeditor E/AndroidRuntime: FATAL EXCEPTION: main
    Process: pl.mrodkiewicz.imageeditor, PID: 13232
    java.lang.OutOfMemoryError
        at android.graphics.Bitmap.nativeCreate(Native Method)
        at android.graphics.Bitmap.createBitmap(Bitmap.java:1026)
        at android.graphics.Bitmap.createBitmap(Bitmap.java:980)
        at android.graphics.Bitmap.createBitmap(Bitmap.java:930)
        at android.graphics.Bitmap.createBitmap(Bitmap.java:891)
        at pl.mrodkiewicz.imageeditor.data.FilterWorkerKt.applyFilter(FilterWorker.kt:23)
        at pl.mrodkiewicz.imageeditor.editor.EditorViewModel$updateFilter$1$1$1.invokeSuspend(EditorViewModel.kt:44)
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:56)
        at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:738)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)

