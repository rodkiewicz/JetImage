package pl.mrodkiewicz.imageeditor.di

import android.content.Context
import androidx.renderscript.RenderScript
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.mrodkiewicz.imageeditor.processor.ColorFilterMatrixImageProcessor
import pl.mrodkiewicz.imageeditor.processor.ConvolutionMatrixImageProcessor
import pl.mrodkiewicz.imageeditor.processor.ImageProcessorManager

@Module
@InstallIn(ApplicationComponent::class)
class ImageProcessorsModule {
    @Provides
    fun provideColorFilterMatrixImageProcessor(@ApplicationContext context: Context,renderScript: RenderScript): ColorFilterMatrixImageProcessor {
        return ColorFilterMatrixImageProcessor(renderScript)
    }
    @Provides
    fun provideConvolutionMatrixImageProcessor(@ApplicationContext context: Context,renderScript: RenderScript): ConvolutionMatrixImageProcessor {
        return ConvolutionMatrixImageProcessor(renderScript)
    }
    @Provides
    fun provideImageProcessorManager(@ApplicationContext context: Context,colorFilterMatrixImageProcessor: ColorFilterMatrixImageProcessor,convolutionMatrixImageProcessor: ConvolutionMatrixImageProcessor): ImageProcessorManager {
        return ImageProcessorManager(colorFilterMIP = colorFilterMatrixImageProcessor,convolutionMIP = convolutionMatrixImageProcessor, context = context)
    }
}
