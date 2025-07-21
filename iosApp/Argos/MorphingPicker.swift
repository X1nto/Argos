//
//  TabView3.swift
//  Argos
//
//  Created by Tornike Khintibidze on 03.03.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct MorphingPicker<Tag: Hashable, Content: View>: View {
    
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.morphingLabelBackgroundStyle) private var backgroundStyle
    
    @Binding var selection: Tag
    @ViewBuilder var content: Content

    var body: some View {
        HStack(spacing: 12) {
            Group(subviews: content) { subviews in
                ForEach(subviews) { view in
                    let tag = view.containerValues.tag(for: Tag.self)!
                    let selected = selection == tag
                    
                    ItemView(
                        selected: selected,
                        title: view.containerValues.morphingLabelTitle,
                        systemIcon: view.containerValues.morphingLabelSystemImage,
                        color: view.containerValues.morphingLabelColor
                    )
                    .onTapGesture {
                        withAnimation(.spring(duration: 0.3)) {
                            selection = tag
                        }
                    }
                }
            }
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
            }
        }
        .foregroundStyle(selected ? .white : Color.primary)
        .frame(minWidth: 56.0, maxWidth: selected ? .infinity : nil)
        .frame(height: 48.0)
        .fontWeight(.semibold)
        .background(selected ? AnyShapeStyle(color) : backgroundStyle)
        .clipShape(.rect(cornerRadius: 20, style: .continuous))
    }
}

struct MorphingLabel: View {
    
    let title: String
    let systemImage: String
    let color: Color
    
    var body: some View {
        Color.clear
            .containerValue(\.morphingLabelTitle, title)
            .containerValue(\.morphingLabelSystemImage, systemImage)
            .containerValue(\.morphingLabelColor, color)
    }
}

extension EnvironmentValues {
    @Entry var morphingLabelBackgroundStyle: AnyShapeStyle = AnyShapeStyle(.thinMaterial)
}

private extension ContainerValues {
    @Entry var morphingLabelTitle: String = ""
    @Entry var morphingLabelSystemImage: String = ""
    @Entry var morphingLabelColor: Color = .clear
}

extension View {
    
    func morphingLabelBackgroundStyle<S: ShapeStyle>(_ style: S) -> some View {
        environment(\.morphingLabelBackgroundStyle, AnyShapeStyle(style))
    }
}

#Preview {
    @Previewable @State var selectedTab: Int = 0
    
    MorphingPicker(selection: $selectedTab) {
        MorphingLabel(title: "Tab 1", systemImage: "person", color: .blue).tag(0)
        MorphingLabel(title: "Tab 2", systemImage: "circle", color: .red).tag(1)
        MorphingLabel(title: "Tab 3", systemImage: "arrowtriangle.up", color: .green).tag(2)
    }
}
