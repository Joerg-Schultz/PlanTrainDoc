package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.YoutubeFragmentBinding

class YouTubeFragment : Fragment() {

    private var _binding: YoutubeFragmentBinding? = null
    private val binding get() = _binding!!


    // https://localcoder.org/youtube-player-support-fragment-no-longer-working-on-android-studio-3-2-android

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = YoutubeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        val youtubePlayerFragment = YouTubePlayerSupportFragmentX.newInstance()
        youtubePlayerFragment.initialize(API_KEY,
            object: YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
                    if (player == null) return
                    if (wasRestored)
                        player.play()
                    else {
                        player.cueVideo("eX4JXxVoVhE")
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                    }
                }
                override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                }
            })
        val fragmentManager = childFragmentManager
        val fragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.youtube_frame_fragment, youtubePlayerFragment)
        fragmentTransaction.commit()
        return view
    }


}