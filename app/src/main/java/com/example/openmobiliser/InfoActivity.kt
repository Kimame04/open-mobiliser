package com.example.openmobiliser

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.openmobiliser.models.Location
import com.example.openmobiliser.models.Locations
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import kotlin.math.roundToInt

class InfoActivity : AppCompatActivity() {
    /*
    @GlideModule
    class AppGlide: AppGlideModule(){

        override fun registerComponents(
            context: android.content.Context,
            glide: Glide,
            registry: Registry
        ) {
            super.registerComponents(context, glide, registry)
            registry.append(
                StorageReference::class.java, InputStream::class.java,
                FirebaseImageLoader.Factory()
            )

        }
    }

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val data: Location = intent.extras?.get("marker") as Location
        val title = findViewById<TextView>(R.id.display_title)
        val desc = findViewById<TextView>(R.id.display_desc)
        val chipGrp = findViewById<ChipGroup>(R.id.display_tags)
        val confirmBtn = findViewById<View>(R.id.display_confirm)
        val disputeBtn = findViewById<View>(R.id.display_dispute)
        val image = findViewById<ImageView>(R.id.display_image)
        val ratio = findViewById<TextView>(R.id.display_ratio)

        title.setText(data.title)
        desc.setText(data.description)
        ratio.setText("Rating: " +
                (100 * data.accepts.toDouble() /(data.accepts.toDouble() + data.disputes.toDouble())).roundToInt()
                + "%"
        )
        val ref = Locations.getImageRef().child(data.imageRef)
        ref.downloadUrl.addOnSuccessListener {
            println("download")
            //image.setImageURI(it)
            Glide.with(applicationContext).load(it).into(image)
            println("download successful")
        }.addOnFailureListener{
            println("failed")
        }
        //Glide.with(applicationContext).load(ref).into(image)

        data.tags.forEach {
            val chip: Chip = Chip(this)
            chip.setText(it)
            chipGrp.addView(chip)
        }

        confirmBtn.setOnClickListener {
            Locations.locations.document(data.id).update("accepts", FieldValue.increment(1))
            Locations.retrieveLocations()
            finish()
        }

        disputeBtn.setOnClickListener {
            Locations.locations.document(data.id).update("disputes", FieldValue.increment(1))
            Locations.retrieveLocations()
            finish()
        }

    }
}