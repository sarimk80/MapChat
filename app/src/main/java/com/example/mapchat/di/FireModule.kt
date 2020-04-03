package com.example.mapchat.di

import com.example.mapchat.R
import com.example.mapchat.repository.FirebaseRepository
import com.example.mapchat.view_model.MapViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val ViewModelModule = module {
    viewModel {
        MapViewModel(get())
    }
}

val RepositoryModule = module {
    single {
        FirebaseRepository(get())
    }
}

val FireModule = module {
    single {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            androidContext().getString(R.string.default_web_client_id)
        ).build()
    }

    single { FirebaseAuth.getInstance() }

    single { Firebase.firestore }


}


