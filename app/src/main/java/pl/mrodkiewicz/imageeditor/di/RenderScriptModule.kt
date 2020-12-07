package pl.mrodkiewicz.imageeditor.di

import android.content.Context
import androidx.renderscript.RenderScript
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
class RenderScriptModule {
    @Provides
    fun provideRenderScript(@ApplicationContext context: Context): RenderScript {
        return RenderScript.create(context)
    }
}