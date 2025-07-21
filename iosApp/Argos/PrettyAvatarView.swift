//
//  PrettyAvatarView.swift
//  Argos
//
//  Created by Tornike Khintibidze on 06.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct PrettyAvatarView: View {
    
    let name: String
    
    private var initials: String {
        name.filter { $0.isUppercase }
    }
    
    var body: some View {
        ZStack(alignment: .center) {
            Circle()
                .fill(gradientFromHash(name))
                .frame(width: 40, height: 40)
            
            Text(initials)
                .font(.headline)
                .foregroundStyle(Color.white)
        }
        .frame(maxWidth: 40, maxHeight: 40)
    }
    
    private func gradientFromHash(_ string: String) -> LinearGradient {
        let baseHue = Double(deterministicHash(for: string) % 360) / 360.0
        let secondHue = fmod(baseHue + 0.1, 1.0)

        let color1 = Color(hue: baseHue, saturation: 0.6, brightness: 0.9)
        let color2 = Color(hue: secondHue, saturation: 0.6, brightness: 0.9)

        return LinearGradient(
            gradient: Gradient(colors: [color1, color2]),
            startPoint: .top,
            endPoint: .bottom
        )
    }
    
    private func deterministicHash(for string: String) -> Int {
        let data = Data(string.utf8)
        var hash: Int = 2166136261
        for byte in data {
            hash ^= Int(byte)
            hash &*= 16777619
        }
        return abs(hash)
    }
}
