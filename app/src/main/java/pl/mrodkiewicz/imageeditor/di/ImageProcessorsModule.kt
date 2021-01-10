package pl.mrodkiewicz.imageeditor.di

import android.content.Context
import androidx.renderscript.RenderScript
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.mrodkiewicz.imageeditor.processor.*
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainImageProcessor

@Module
@InstallIn(ApplicationComponent::class)
class ImageProcessorsModule {


    @Provides
    fun provideColorFilterMatrixImageProcessor(
        renderScript: RenderScript
    ): ColorFilterMatrixImageProcessor {
        return ColorFilterMatrixImageProcessor(renderScript)
    }

    @Provides
    fun provideConvolutionMatrixImageProcessor(
        renderScript: RenderScript
    ): ConvolutionMatrixImageProcessor {
        return ConvolutionMatrixImageProcessor(renderScript)
    }

    @Provides
    fun provideBlurImageProcessor(
        renderScript: RenderScript
    ): BlurImageProcessor {
        return BlurImageProcessor(renderScript)
    }
    @Provides
    fun provideLutImageProcessor(
        renderScript: RenderScript
    ): LutImageProcessor {
        return LutImageProcessor(renderScript)
    }

    @MainImageProcessor
    @Provides
    fun provideImageProcessorManager(
        @ApplicationContext context: Context,
        colorFilterMatrixImageProcessor: ColorFilterMatrixImageProcessor,
        convolutionMatrixImageProcessor: ConvolutionMatrixImageProcessor,
        blurImageProcessor: BlurImageProcessor,
        lutImageProcessor: LutImageProcessor,
    ): ImageProcessor {
        return ImageProcessorManager(
            colorFilterMIP = colorFilterMatrixImageProcessor,
            convolutionMIP = convolutionMatrixImageProcessor,
            blurIP = blurImageProcessor,
            lutIP = lutImageProcessor,
            context = context
        )
    }
}
