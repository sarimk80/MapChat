package com.example.mapchat.di

import com.example.mapchat.R
import com.example.mapchat.repository.FirebaseRepository
import com.example.mapchat.view_model.ChatViewModel
import com.example.mapchat.view_model.MapViewModel
import com.example.mapchat.view_model.SettingViewModel
import com.example.mapchat.view_model.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mapVewModelModule = module {
    viewModel {
        MapViewModel(get())
    }
    viewModel {
        SettingViewModel(get())
    }
}

val chatViewModelModule = module {
    viewModel {
        ChatViewModel(get())
    }

}
val userViewModelModule = module {
    viewModel {
        UserViewModel(get())
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
        ).requestEmail().requestProfile().build()
    }

    single { FirebaseAuth.getInstance() }

    single { Firebase.firestore }


}


