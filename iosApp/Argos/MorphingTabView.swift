//
//  TabView3.swift
//  Argos
//
//  Created by Tornike Khintibidze on 03.03.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct MorphingTabView<Content: View, Tag: Hashable>: View {
    
    @Binding var selection: Tag?
    @ViewBuilder let content: Content
    
    @Environment(\.colorScheme) private var colorScheme
    
    init(selection: Binding<Tag>, @ViewBuilder content: () -> Content) {
        self._selection = .init(
            get: { selection.wrappedValue },
            set: {
                if let value = $0 {
                    selection.wrappedValue = value
                }
            }
        )
        self.content = content()
    }
    
    var body: some View {
        VStack(spacing: 12) {
            HStack(spacing: -6) {
                Group(subviews: content) { subviews in
                    ForEach(subviews) { subview in
                        let tag = subview.containerValues.tag(for: Tag.self)
                        let selected = selection == tag
                        
                        Color.clear
                            .frame(minWidth: 0, maxWidth: 6, maxHeight: 0)
                        
                        ItemView(
                            selected: selected,
                            title: subview.containerValues.morphingTabTitle,
                            systemIcon: subview.containerValues.morphingTabIcon,
                            color: subview.containerValues.morphingTabColor
                        )
                        .onTapGesture {
                            withAnimation(.spring(duration: 0.2)) {
                                selection = tag
                            }
                        }
                        
                        Color.clear
                            .frame(minWidth: 0, maxWidth: 6, maxHeight: 0)
                    }
                }
            }
            .padding(.horizontal, 6)
            
            Group(subviews: content) { subviews in
                ForEach(subviews) { subview in
                    let tag = subview.containerValues.tag(for: Tag.self)
                    if (selection == tag) {
                        subview
                    }
                }
            }
            .frame(maxHeight: .infinity, alignment: .top)
        }
        
    }
    
    @ViewBuilder
    private func ItemView(
        selected: Bool,
        title: String,
        systemIcon: String,
        color: Color
    ) -> some View {
        HStack {
            Image(systemName: systemIcon)
                .symbolVariant(selected ? .fill : .none)
            if (selected) {
                Text(title)
                    .font(.callout)
                    .lineLimit(1)
                    .fixedSize(horizontal: true, vertical: false)
//                    .frame(minWidth: 0, maxWidth: selected ? nil : 0)
            }
        }
        .foregroundStyle(selected ? .white : Color.primary)
        .frame(minWidth: 56.0, maxWidth: selected ? .infinity : nil)
        .frame(height: 48.0)
        .fontWeight(.semibold)
        .background(selected ? AnyShapeStyle(color) : AnyShapeStyle(.thinMaterial))
        .clipShape(.rect(cornerRadius: 20, style: .continuous))
    }
}

extension ContainerValues {
    @Entry var morphingTabTitle: String = ""
    @Entry var morphingTabIcon: String = ""
    @Entry var morphingTabColor: Color = Color.gray
}

extension View {
    
    func morphingTab(_ title: String, systemIcon: String, color: Color) -> some View {
        return self
            .containerValue(\.morphingTabTitle, title)
            .containerValue(\.morphingTabIcon, systemIcon)
            .containerValue(\.morphingTabColor, color)
    }
}

#Preview {
    @Previewable @State var count: Int = 0
    @Previewable @State var selectedTab: Int = 0
    
    MorphingTabView(selection: $selectedTab) {
        LazyVStack {
            ForEach(0..<20) { i in
                Text("Item \(i)")
            }
        }
        .morphingTab("20 items", systemIcon: "list.bullet", color: .blue)
        .tag(0)
        
        VStack {
            Text("\(count)")
            Button(action: {
                count += 1
            }) {
                Text("Increase")
            }
        }
        .morphingTab("Incrementor", systemIcon: "plus", color: .green)
        .tag(1)
    }
}
