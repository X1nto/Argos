//
//  Home Screen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 21.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct HomeScreen: View {
    @State var selectedIndex: Int = 0
    
    var body: some View {
        TabPager(
            selectedIndex: $selectedIndex,
            items: [
                "page 1": "kaci shedis saxinkleshi",
                "page 2": "99 xinkali mindao",
                "page 3": "99 ra barem 100 aigeo",
                "page 4": "100 ra gori kiar varo",
                "page 5": "muteli",
                "page 6": "yle",
            ],
            tabContent: { tab in
                Text(tab)
            }
        ) { page in
            Text(page)
        }
    }
}

#Preview {
    HomeScreen()
}
