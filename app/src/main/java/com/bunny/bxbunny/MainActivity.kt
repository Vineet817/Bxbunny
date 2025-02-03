package com.bunny.bxbunny
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bunny.bxbunny.ui.theme.BxbunnyTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.PeerConnectionFactory

class MainActivity : ComponentActivity() {
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

    }
    fun createPeerConnectionFactory(context: Context): PeerConnectionFactory {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    EglBase.create().eglBaseContext, true, true)
            )
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(EglBase.create().eglBaseContext))
            .createPeerConnectionFactory()
    }
    fun sendOffer(sdp: String) {
        val callRef = db.collection("calls").document("callID")
        val offer = hashMapOf("sdp" to sdp, "type" to "offer")
        callRef.set(offer)
    }
    fun listenForAnswer() {
        val callRef = db.collection("calls").document("callID")
        callRef.addSnapshotListener { snapshot, _ ->
            val answerSdp = snapshot?.getString("sdp")
            if (answerSdp != null) {
                // Set remote SDP
            }
        }
    }
    fun sendIceCandidate(candidate: IceCandidate) {
        val callRef = db.collection("candidates").document("callID")
        val iceData = hashMapOf("candidate" to candidate.sdp, "sdpMLineIndex" to candidate.sdpMLineIndex)
        callRef.set(iceData)
    }
    fun listenForIceCandidates() {
        val callRef = db.collection("candidates").document("callID")
        callRef.addSnapshotListener { snapshot, _ ->
            val candidate = IceCandidate(
                snapshot?.getString("sdpMid"),
                snapshot?.getLong("sdpMLineIndex")?.toInt() ?: 0,
                snapshot?.getString("candidate") ?: ""
            )
            // Add candidate to peer connection
        }
    }

}


