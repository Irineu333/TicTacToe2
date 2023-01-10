package com.neo.hash.annotation

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "dark", group = "theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "light", group = "theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
annotation class ThemesPreview