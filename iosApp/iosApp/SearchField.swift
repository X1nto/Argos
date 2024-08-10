//
//  SearchField.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 11.08.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import UIKit

struct SearchField : UIViewRepresentable {
    @Binding var text: String
    let placeholder: String?
    
    func updateUIView(_ uiView: UISearchBar, context: Context) {
        uiView.text = text
    }

    
    func makeUIView(context: Context) -> UISearchBar {
        let searchBar = UISearchBar()
        searchBar.delegate = context.coordinator
        searchBar.placeholder = placeholder
        searchBar.backgroundImage = UIImage()
        return searchBar
    }
    
    func makeCoordinator() -> Coordinator {
        return Coordinator(text: $text)
    }

    class Coordinator: NSObject, UISearchBarDelegate {
        @Binding var text: String
        
        init(text: Binding<String>) {
            self._text = text
        }
        
        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            text = searchText
        }
    }
}

#Preview {
    SearchField(
        text: .constant("Hello"),
        placeholder: "Placeholder"
    )
}
