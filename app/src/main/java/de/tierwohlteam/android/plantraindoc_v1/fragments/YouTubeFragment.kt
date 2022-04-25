package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.YoutubeFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.keys.API_KEY
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class YouTubeFragment : Fragment() {

    private var _binding: YoutubeFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalViewModel by activityViewModels()

    // https://localcoder.org/youtube-player-support-fragment-no-longer-working-on-android-studio-3-2-android

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = YoutubeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.tvYoutubeGoal.text = viewModel.selectedGoal.value?.goal?.goal ?: ""
        val videoID = viewModel.selectedGoal.value?.goal?.youtube ?: ""
        if (videoID.isNotEmpty()) {
            binding.tvNovideo.visibility = View.GONE
            val youtubePlayerFragment = YouTubePlayerSupportFragmentX.newInstance()
            youtubePlayerFragment.initialize(API_KEY,
                object : YouTubePlayer.OnInitializedListener {
                    override fun onInitializationSuccess(
                        provider: YouTubePlayer.Provider?,
                        player: YouTubePlayer?,
                        wasRestored: Boolean
                    ) {
                        if (player == null) return
                        if (wasRestored)
                            player.play()
                        else {
                            player.cueVideo(videoID)
                            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                        }
                    }

                    override fun onInitializationFailure(
                        p0: YouTubePlayer.Provider?,
                        p1: YouTubeInitializationResult?
                    ) {
                    }
                })
            val fragmentManager = childFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.youtube_frame_fragment, youtubePlayerFragment)
            fragmentTransaction.commit()
        }
        return view
    }
}