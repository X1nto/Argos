//
//  Searchable.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 27.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

extension View {
    func searchable(text: Binding<String>, isVisible: Bool) -> some View {
        Group {
            if isVisible {
                self.searchable(text: text)
            } else {
                self
            }
        }
    }
}
