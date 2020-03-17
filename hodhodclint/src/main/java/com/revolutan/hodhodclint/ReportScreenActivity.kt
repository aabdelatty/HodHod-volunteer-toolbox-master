package com.revolutan.hodhodclint

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.revolutan.hodhodclint.adapter.IssuesAdapter
import com.revolutan.hodhodclint.dto.*
import kotlinx.android.synthetic.main.activity_report_screen.*
import java.io.InputStream


class ReportScreenActivity : AppCompatActivity(), IssuesAdapter.OnItemClick, PermissionListener {


    private var listening = false
    private var speechService: SpeechToText? = null
    private var capture: InputStream? = null
    //    private val recoTokens: SpeakerLabelsDiarization.RecoTokens? = null
    private var microphoneHelper: MicrophoneHelper? = null
    private val database = FirebaseDatabase.getInstance()
    private val reporter = generateRandomReporter()
    private var listOFIssues = getIssuesTypes()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_screen)
        recordFAB.setOnClickListener {

            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(this)
                    .check();

        }

        setSupportActionBar(myToolBar)

        microphoneHelper = MicrophoneHelper(this);


        issuesRecyclerView.apply {
            adapter = IssuesAdapter(listOFIssues, this@ReportScreenActivity)
            layoutManager = GridLayoutManager(this@ReportScreenActivity, 2)
        }
        val reporterRef = database.getReference("reporters")
        reporterRef.child(reporter.name).setValue(reporter)
    }

    override fun onClick(item: Issue) {
        sendIssue(item)

    }


    fun sendIssue(item: Issue) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("${item.type} status")

        item.apply {
            reporterName = reporter.name
            val randomLocation =
                    getLocation(31.2213, 29.9379, 20)
            lat = randomLocation.latitude
            lng = randomLocation.longitude
        }

        val reportsRef = database.getReference("reports")

        reportsRef.push().setValue(item).addOnFailureListener {
            builder.setMessage("Please try again.")
            builder.show()
        }.addOnSuccessListener {
            builder.setMessage("Issue has been submitted successfully ")
            builder.show()
        }
    }

    //Record a message via Watson Speech to Text
    private fun recordMessage() {
        speechService = SpeechToText()
        //Use "apikey" as username and apikey as your password
        speechService?.setUsernameAndPassword("apikey", "iWKIYy1t39HGzUVUz6OKRFtXiyFbUoHh7Gy3XEEgXBP_")
        //Default: https://stream.watsonplatform.net/text-to-speech/api
        speechService?.endPoint = "https://gateway-lon.watsonplatform.net/speech-to-text/api"

        if (!listening) {
            capture = microphoneHelper?.getInputStream(true)
            Thread(Runnable {
                try {
                    speechService?.recognizeUsingWebSocket(getRecognizeOptions(capture), MicrophoneRecognizeDelegate())
                } catch (e: Exception) {
                    showError(e)
                }
            }).start()
            listening = true
            Toast.makeText(this, "Listening....Click to Stop", Toast.LENGTH_LONG).show()

        } else {
            try {
                microphoneHelper?.closeInputStream()
                listening = false
                Toast.makeText(this, "Stopped Listening....Click to Start", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Check Internet Connection
     * @return
     */
    private fun checkInternetConnection(): Boolean {
        // get Connectivity Manager object to check connection
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        // Check for network connections
        if (isConnected) {
            return true
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show()
            return false
        }

    }

    //Private Methods - Speech to Text
    private fun getRecognizeOptions(audio: InputStream?): RecognizeOptions {
        return RecognizeOptions.Builder()
                .audio(audio)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                //TODO: Uncomment this to enable Speaker Diarization
                //.speakerLabels(true)
                .build()
    }

    private inner class MicrophoneRecognizeDelegate : BaseRecognizeCallback() {

        override fun onTranscription(speechResults: SpeechRecognitionResults?) {
            println(speechResults)
            //TODO: Uncomment this to enable Speaker Diarization
            /*SpeakerLabelsDiarization.RecoTokens recoTokens = new SpeakerLabelsDiarization.RecoTokens();
            if(speechResults.getSpeakerLabels() !=null)
            {
                recoTokens.add(speechResults);
                Log.i("SPEECHRESULTS",speechResults.getSpeakerLabels().get(0).toString());
            }*/
            if (speechResults!!.results != null && !speechResults.results.isEmpty()) {
                val text = speechResults.results[0].alternatives[0].transcript
                if (speechResults.results.first().isFinalResults)
                    showMicText(text)
            }
        }

        override fun onConnected() {

        }

        override fun onError(e: Exception) {
            showError(e)
            enableMicButton()
        }

        override fun onDisconnected() {
            enableMicButton()
        }

        override fun onInactivityTimeout(runtimeException: RuntimeException?) {

        }

        override fun onListening() {

        }

        override fun onTranscriptionComplete() {

        }
    }

    private fun showMicText(text: String) {
        Log.d(javaClass.name, text)

        var max = 0.0
        var problem = Problems.MedicalAssistance.value
        listOFIssues.forEach {
            val sim = similarity(text, it.type)
            if (sim > max) {
                max = sim
                problem = it.type
            }
        }

        sendIssue(Issue(problem))

    }

    private fun enableMicButton() {
        runOnUiThread { recordFAB.isEnabled = true }
    }

    private fun showError(e: Exception) {
        runOnUiThread {
            Toast.makeText(this@ReportScreenActivity, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        recordMessage()

    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {

    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {

    }


    fun similarity(s1: String, s2: String): Double {
        var longer = s1
        var shorter = s2
        if (s1.length < s2.length) { // longer should always have greater length
            longer = s2
            shorter = s1
        }
        val longerLength = longer.length
        return if (longerLength == 0) {
            1.0 /* both strings are zero length */
        } else (longerLength - editDistance(longer, shorter)) / longerLength.toDouble()
        /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    fun editDistance(s1: String, s2: String): Int {
        var s1 = s1
        var s2 = s2
        s1 = s1.toLowerCase()
        s2 = s2.toLowerCase()

        val costs = IntArray(s2.length + 1)
        for (i in 0..s1.length) {
            var lastValue = i
            for (j in 0..s2.length) {
                if (i == 0)
                    costs[j] = j
                else {
                    if (j > 0) {
                        var newValue = costs[j - 1]
                        if (s1[i - 1] != s2[j - 1])
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1
                        costs[j - 1] = lastValue
                        lastValue = newValue
                    }
                }
            }
            if (i > 0)
                costs[s2.length] = lastValue
        }
        return costs[s2.length]
    }

}
