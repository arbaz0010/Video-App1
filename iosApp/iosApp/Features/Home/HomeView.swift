import SwiftUI

struct HomeView: View {
    var body: some View {
        VStack(spacing: 12) {
            Text("Hello, KMP")
                .font(AppTypography.headline)
                .foregroundColor(AppColors.textDark)
            Text("Edit HomeView.swift to start building.")
                .font(AppTypography.cardBody)
                .foregroundColor(AppColors.textMid)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(AppColors.background.ignoresSafeArea())
    }
}

#Preview {
    HomeView()
}
