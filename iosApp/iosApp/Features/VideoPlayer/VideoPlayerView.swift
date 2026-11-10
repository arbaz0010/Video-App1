import SwiftUI
import AVKit
import Shared

struct VideoPlayerView: View {
    let videoId: String
    @Environment(\.dismiss) var dismiss
    
    @State private var videoTitle: String = ""
    @State private var videoUrl: String = ""
    @State private var isPlaying: Bool = false
    @State private var isBuffering: Bool = false
    @State private var currentTime: Double = 0
    @State private var duration: Double = 0
    
    private let store = KoinHelper.shared.videoPlayerStore
    private var player: AVPlayer?
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // ویڈیو پلیر
                if !videoUrl.isEmpty {
                    VideoPlayer(player: player)
                        .aspectRatio(16/9, contentMode: .fit)
                        .onAppear {
                            if let url = URL(string: videoUrl) {
                                player = AVPlayer(url: url)
                                if isPlaying {
                                    player?.play()
                                }
                            }
                        }
                        .onDisappear {
                            player?.pause()
                        }
                } else if isBuffering {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .frame(height: 200)
                }
                
                // کنٹرولز
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        Text(videoTitle)
                            .font(.headline)
                        
                        // پروگریس
                        VStack(spacing: 4) {
                            Slider(value: $currentTime, in: 0...max(duration, 1)) { editing in
                                if !editing {
                                    player?.seek(to: CMTime(seconds: currentTime, preferredTimescale: 600))
                                }
                            }
                            
                            HStack {
                                Text(formatTime(currentTime))
                                Spacer()
                                Text(formatTime(duration))
                            }
                            .font(.caption)
                            .foregroundColor(.secondary)
                        }
                        
                        // پلے/پاز
                        HStack {
                            Spacer()
                            Button(action: {
                                if isPlaying {
                                    store.dispatch(intent: VideoPlayerIntent.Pause.shared)
                                    player?.pause()
                                } else {
                                    store.dispatch(intent: VideoPlayerIntent.Play.shared)
                                    player?.play()
                                }
                            }) {
                                Image(systemName: isPlaying ? "pause.circle.fill" : "play.circle.fill")
                                    .font(.system(size: 60))
                            }
                            .disabled(videoUrl.isEmpty || isBuffering)
                            Spacer()
                        }
                        
                        if isBuffering {
                            ProgressView()
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .padding()
                }
            }
            .navigationTitle("ویڈیو پلیر")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("واپس") {
                        dismiss()
                    }
                }
            }
            .onAppear {
                store.dispatch(intent: VideoPlayerIntent.LoadVideo(videoId: videoId))
            }
            .task {
                for await state in store.state {
                    videoTitle = state.videoTitle
                    videoUrl = state.videoUrl
                    isPlaying = state.isPlaying
                    isBuffering = state.isBuffering
                    currentTime = Double(state.currentTime)
                    duration = Double(state.duration)
                }
            }
        }
    }
    
    private func formatTime(_ seconds: Double) -> String {
        let mins = Int(seconds) / 60
        let secs = Int(seconds) % 60
        return String(format: "%d:%02d", mins, secs)
    }
}
