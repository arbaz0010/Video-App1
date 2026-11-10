import SwiftUI
import Shared

struct VideoListView: View {
    @State private var videos: [Video] = []
    @State private var isLoading: Bool = false
    @State private var error: String? = nil
    
    private let store = KoinHelper.shared.videoListStore
    
    var body: some View {
        NavigationStack {
            Group {
                if isLoading && videos.isEmpty {
                    ProgressView()
                } else if let error = error, videos.isEmpty {
                    VStack {
                        Text(error)
                        Button("دوبارہ کوشش کریں") {
                            store.dispatch(intent: VideoListIntent.LoadVideos.shared)
                        }
                    }
                } else if videos.isEmpty {
                    Text("کوئی ویڈیو نہیں")
                } else {
                    List(videos) { video in
                        NavigationLink(destination: VideoPlayerView(videoId: video.id)) {
                            VideoRow(video: video)
                        }
                    }
                    .refreshable {
                        store.dispatch(intent: VideoListIntent.RefreshVideos.shared)
                    }
                }
            }
            .navigationTitle("ویڈیوز")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        store.dispatch(intent: VideoListIntent.RefreshVideos.shared)
                    }) {
                        Image(systemName: "arrow.clockwise")
                    }
                }
            }
        }
        .task {
            for await state in store.state {
                videos = state.videos
                isLoading = state.isLoading
                error = state.error
            }
        }
    }
}

struct VideoRow: View {
    let video: Video
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(video.title)
                .font(.headline)
                .lineLimit(2)
            
            Text(video.description)
                .font(.caption)
                .lineLimit(1)
                .foregroundColor(.secondary)
            
            Text("\(video.formattedDuration) • \(video.formattedDate)")
                .font(.caption2)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
    }
}
