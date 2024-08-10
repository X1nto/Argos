//
//  TabPager.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 21.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import OrderedCollections

struct TabPager<Tab: Hashable, Page, TabContent: View, PageContent: View>: View {
    
    @Binding var selected: Int
    var items: OrderedDictionary<Tab, Page>
    var tabContent: (Tab) -> TabContent
    var pageContent: (Page) -> PageContent
    
    init(
        selectedIndex: Binding<Int>,
        items: OrderedDictionary<Tab, Page>,
        @ViewBuilder tabContent: @escaping (Tab) -> TabContent,
        @ViewBuilder content: @escaping (Page) -> PageContent
    ) {
        self._selected = selectedIndex
        self.items = items
        self.tabContent = tabContent
        self.pageContent = content
    }
    
    var body: some View {
        VStack {
            ScrollViewReader { value in
                ScrollView(.horizontal) {
                    HStack(spacing: 20) {
                        ForEach(Array(items.keys.enumerated()), id: \.offset) { index, tab in
                            Button(action: {
                                selected = index
                            }) {
                                tabContent(tab)
                                    .foregroundStyle(selected == index ? Color.accentColor : .secondary)
                                    .animation(.easeIn, value: selected)
                            }
                        }
                        .buttonStyle(.plain)
                    }
                    .padding(.all, CGFloat(16.0))
                    .scrollTargetLayout()
                }
                .scrollIndicators(.hidden)
                .scrollPosition(id: Binding($selected), anchor: .center)
            }
            GeometryReader { size in
                ScrollView(.horizontal) {
                    LazyHStack(content: {
                        ForEach(Array(items.values.enumerated()), id: \.offset) { index, page in
                            pageContent(page)
                                .frame(width: size.size.width, height: size.size.height)
                        }
                    })
                    .scrollTargetLayout()
                }
                .scrollTargetBehavior(.viewAligned)
                .scrollPosition(id: Binding($selected))
                .scrollIndicators(.hidden)
            }
        }
    }
}


#Preview {
    @State var test: Int = 0
    
    return TabPager(
        selectedIndex: $test,
        items: [
            "page 1": "Hello",
            "page 2": "World!"
        ],
        tabContent: { tab in
            Text(tab)
        }
    ) { page in
        Text(page)
    }
}
