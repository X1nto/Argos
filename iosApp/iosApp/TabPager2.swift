//
//  TabPager2.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 24.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct TabPagePreferenceKey : PreferenceKey {
    
    typealias Value = [String]
    
    static var defaultValue: Value = []
    
    static func reduce(value: inout Value, nextValue: () -> Value) {
        value.append(contentsOf: nextValue())
    }
}

extension View {
    func tabPage(_ title: String) -> some View {
        self.preference(key: TabPagePreferenceKey.self, value: [title])
    }
}

struct TabPager2 : View {
    
    var body: some View {
        VStack {
//            ScrollView(.horizontal) {
//                HStack(spacing: 20) {
//                    ForEach(Array(items.keys.enumerated()), id: \.offset) { index, tab in
//                        Button(action: {
//                            selected = index
//                        }) {
//                            tabContent(tab)
//                                .foregroundStyle(selected == index ? Color.accentColor : .secondary)
//                                .animation(.easeIn, value: selected)
//                        }
//                    }
//                    .buttonStyle(.plain)
//                }
//                .padding(.all, CGFloat(16.0))
//            }
//            .scrollIndicators(.hidden)
//            .scrollPosition(id: Binding($selected))
//            
//            GeometryReader { size in
//                ScrollView(.horizontal) {
//                    LazyHStack(content: {
//                        ForEach(Array(items.values.enumerated()), id: \.offset) { index, page in
//                            pageContent(page)
//                                .frame(width: size.size.width, height: size.size.height)
//                        }
//                    })
//                    .scrollTargetLayout()
//                }
//                .scrollTargetBehavior(.paging)
//                .scrollPosition(id: Binding($selected))
//                .scrollIndicators(.hidden)
//            }
        }
    }
    
}

#Preview {
    TabPager2()
}
