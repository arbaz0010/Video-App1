import SwiftUI
import Shared
import UniformTypeIdentifiers

struct VideoUploadView: View {
    @Environment(\.dismiss) var dismiss
    @State private var title: String = ""
    @State private var description: String = ""
    @State private var selectedFile: URL? = nil
    @State private var showFilePicker = false
    
    private let store = KoinHelper.shared.videoUploadStore
    
    var body: some View {
        NavigationStack {
            Form {
                Section("تفصیلات") {
                    TextField("عنوان", text: $title)
                    TextField("تفصیل", text: $description, axis: .vertical)
                        .lineLimit(3...6)
                }
                
                Section("ویڈیو") {
                    if let file = selectedFile {
                        Text(file.lastPathComponent)
                            .font(.caption)
                    } else {
                        Text("کوئی فائل منتخب نہیں")
                            .foregroundColor(.secondary)
                    }
                    
                    Button("ویڈیو منتخب کریں") {
                        showFilePicker = true
                    }
                }
                
                Section {
                    Button("اپلوڈ کریں") {
                        store.dispatch(intent: VideoUploadIntent.UpdateTitle(title: title))
                        store.dispatch(intent: VideoUploadIntent.UpdateDescription(description: description))
                        if let path = selectedFile?.path {
                            store.dispatch(intent: VideoUploadIntent.FileSelected(filePath: path))
                        }
                        store.dispatch(intent: VideoUploadIntent.UploadVideo.shared)
                    }
                    .disabled(title.isEmpty || selectedFile == nil)
                }
            }
            .navigationTitle("ویڈیو اپلوڈ")
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("واپس") {
                        dismiss()
                    }
                }
            }
            .fileImporter(
                isPresented: $showFilePicker,
                allowedContentTypes: [UTType.movie],
                allowsMultipleSelection: false
            ) { result in
                if case .success(let urls) = result, let url = urls.first {
                    selectedFile = url
                }
            }
            .task {
                for await state in store.state {
                    title = state.title
                    description = state.description
                    if let path = state.selectedFilePath {
                        selectedFile = URL(fileURLWithPath: path)
                    }
                }
            }
            .task {
                for await effect in store.effects {
                    if effect is VideoUploadEffect.UploadSuccess {
                        dismiss()
                    }
                }
            }
        }
    }
}
