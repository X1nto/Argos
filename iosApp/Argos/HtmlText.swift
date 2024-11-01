//
//  HtmlText.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 06.08.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct HtmlText: View {
    let text: String
    
    init(_ text: String) {
        self.text = text
    }
    
    private var attributedString: AttributedString? {
        guard let nsAttributedString = try? NSAttributedString(
            data: text.data(using: .utf16)!,
            options: [.documentType: NSAttributedString.DocumentType.html],
            documentAttributes: nil
        ) else { return nil }
        
        return try? AttributedString(nsAttributedString, including: \.uiKit)
    }
    
    var body: some View {
        if let attributedString = attributedString {
            Text(attributedString)
        } else {
            Text(text)
        }
    }
}

#Preview {
    HtmlText("Test")
}
