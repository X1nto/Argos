//
//  List.swift
//  Argos
//
//  Created by Tornike Khintibidze on 02.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct InsetGroupedSection<Header: View, Content: View>: View {
    
    @Environment(\.colorScheme) private var colorScheme
    
    @ViewBuilder var content: Content
    @ViewBuilder var header: Header
    
    private var rowColor: AnyShapeStyle {
        switch colorScheme {
        case .dark:
//            AnyShapeStyle(.regularMaterial)
            AnyShapeStyle(Color(UIColor.systemGray6))
        case .light:
            AnyShapeStyle(Color(UIColor.systemBackground))
        }
    }
    
    var body: some View {
        Section {
            VStack(spacing: 0) {
                content
            }
            .background(rowColor)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .padding(.horizontal)
            .padding(.bottom)
        } header: {
            header
                .font(.title3)
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)
                .padding(.vertical, 8)
        }
    }
    
}

extension InsetGroupedSection where Header == Text {
    
    init(_ title: LocalizedStringKey, @ViewBuilder content: () -> Content) {
        self.content = content()
        self.header = Text(title)
    }
    
}

struct ListNavigationLink<Label: View, Value: Hashable>: View {
    
    @Environment(\.showSeparator) private var showSeparator
    
    let value: Value
    @ViewBuilder let label: Label
    
    var body: some View {
        NavigationLink(value: value) {
            VStack(spacing: 0) {
                HStack(spacing: 8) {
                    label
                    
                    Spacer()
                    
                    Chevron()
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .contentShape(Rectangle()) //To make Spacer() areas clickable
                
                if showSeparator {
                    Divider().padding(.leading)
                }
            }
        }
        .buttonStyle(ListSelectionStyle())
    }
}

struct Chevron: View {
    
    var body: some View {
        Image(systemName: "chevron.right")
            .font(.footnote)
            .fontWeight(.bold)
            .foregroundStyle(Color(UIColor.systemGray3))
    }
}

extension EnvironmentValues {
    @Entry var showSeparator: Bool = true
}

extension View {
    
    func separatorVisible(_ visible: Bool) -> some View {
        environment(\.showSeparator, visible)
    }
}

private struct ListSelectionStyle: ButtonStyle {
    
    @Environment(\.colorScheme) private var colorScheme
    
    func makeBody(configuration: Self.Configuration) -> some View {
        configuration.label
            .background(configuration.isPressed ? Color(UIColor.systemGray4) : Color.clear)
    }
}
