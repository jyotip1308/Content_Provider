package com.android.content_provider

import android.content.ContentUris
import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.android.content_provider.ui.theme.Content_ProviderTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ImageViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("MMM","4")

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                0
            )
        }else{
            Log.d("MMM NOT","4")

        }
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,

        )
        val millisYesterday = Calendar.getInstance().apply {
           add(Calendar.DAY_OF_YEAR,0)
        }.timeInMillis
        val selection = "${MediaStore.Images.Media.DATE_TAKEN } >= ?"
        val selectionArgs = arrayOf(millisYesterday.toString())

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val images = mutableListOf<Image>()
            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                images.add(Image(id,name, uri))
            }
             viewModel.updateImages(images)
        }
        setContent {
            Content_ProviderTheme {
                Log.d("MMM","1")
                LazyColumn(modifier = Modifier.fillMaxSize()){
                    items(viewModel.images){image ->
                        Column (modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Log.d("MMM","2")

                            AsyncImage(model = image.uri,
                                contentDescription = null)
                            Text(text = image.name)
                            Log.d("MMM","3")

                        }
                    }
                }
            }

        }
    }
}

data class Image(
    val id: Long,
    val name: String,
    val uri: Uri
)

