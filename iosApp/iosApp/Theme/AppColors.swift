import SwiftUI

enum AppColors {
    static let background = Color(hex: 0xFAFAF9)
    static let surface = Color.white
    static let primary = Color(hex: 0x3B82F6)
    static let textDark = Color(hex: 0x111827)
    static let textMid = Color(hex: 0x4B5563)
    static let textMuted = Color(hex: 0x9CA3AF)
    static let border = Color(hex: 0xE5E7EB)
    static let error = Color(hex: 0xEF4444)
}

extension Color {
    init(hex: UInt, alpha: Double = 1.0) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255.0,
            green: Double((hex >> 8) & 0xFF) / 255.0,
            blue: Double(hex & 0xFF) / 255.0,
            opacity: alpha
        )
    }
}
