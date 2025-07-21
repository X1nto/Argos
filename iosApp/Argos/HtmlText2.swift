//
//  Untitled.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 30.09.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit
import SwiftUI
import WebKit

private protocol CssRepresentable {
    var css: String { get }
}

extension Color: CssRepresentable {
    
    var css: String {
        let resolved = self.resolve(in: .init())
        let normalRed = lroundf(Float(resolved.red * 255))
        let normalGreen = lroundf(Float(resolved.green * 255))
        let normalBlue = lroundf(Float(resolved.blue * 255))

        let hexString = String.init(format: "#%02lX%02lX%02lX", normalRed, normalGreen, normalBlue)
        return hexString
    }
}

extension Font.Weight: CssRepresentable {
    var css: String {
        switch self {
        case .ultraLight: "100"
        case .thin: "200"
        case .light: "300"
        case .regular: "400"
        case .medium: "500"
        case .semibold: "600"
        case .bold: "700"
        case .heavy: "800"
        case .black: "900"
        default: "400"
        }
    }
}

extension EdgeInsets: CssRepresentable {
    var css: String {
        "\(top)px \(leading)px \(bottom)px \(trailing)px"
    }
}

struct HtmlText2TextStyle: CssRepresentable {
    let color: Color
    let fontSize: CGFloat
    let fontWeight: Font.Weight
    
    var css: String {
        """
        color: \(color.css);
        font-family: -apple-system;
        font-weight: \(fontWeight.css);
        font-size: \(fontSize);
        """
    }
}

struct HtmlText2ContentStyle: CssRepresentable {
    let h1: HtmlText2TextStyle
    let h2: HtmlText2TextStyle
    let h3: HtmlText2TextStyle
    let h4: HtmlText2TextStyle
    let h5: HtmlText2TextStyle
    let h6: HtmlText2TextStyle
    let body: HtmlText2TextStyle
    let a: HtmlText2TextStyle
    
    var css: String {
        """
        * {
            margin: 0; 
            padding: 0;
        }
        h1 {
            \(h1.css)
        }
        h2 {
            \(h2.css)
        }
        h3 {
            \(h3.css)
        }
        h4 {
            \(h4.css)
        }
        h5 {
            \(h5.css)
        }
        p, body {
            \(body.css)
        }
        a {
            \(a.css)
        }
        """
    }
}

private struct HtmlText2ContentStyleKey: EnvironmentKey {
    static let defaultValue: HtmlText2ContentStyle? = nil
}

private struct HtmlText2ContentInsetsKey: EnvironmentKey {
    static let defaultValue: EdgeInsets = .init()
}

private struct HtmlText2ScrollOffsetKey: EnvironmentKey {
    static var defaultValue: (CGPoint) -> Void = { _ in }
}

extension EnvironmentValues {
    
    var htmlText2ContentStyle: HtmlText2ContentStyle? {
        get {
            self[HtmlText2ContentStyleKey.self]
        }
        set {
            self[HtmlText2ContentStyleKey.self] = newValue
        }
    }
    
    var htmlText2ContentInsets: EdgeInsets {
        get {
            self[HtmlText2ContentInsetsKey.self]
        }
        set {
            self[HtmlText2ContentInsetsKey.self] = newValue
        }
    }
    
    var htmlText2ScrollOffset: (CGPoint) -> Void {
        get {
            self[HtmlText2ScrollOffsetKey.self]
        }
        set {
            self[HtmlText2ScrollOffsetKey.self] = newValue
        }
    }
}

extension View {
    
    func htmlText2ContentStyle(_ style: HtmlText2ContentStyle) -> some View {
        environment(\.htmlText2ContentStyle, style)
    }
    
    func htmlText2ContentPadding(_ insets: EdgeInsets) -> some View {
        environment(\.htmlText2ContentInsets, insets)
    }
    
    func htmlText2ContentPadding(_ length: CGFloat) -> some View {
        htmlText2ContentPadding(EdgeInsets(top: length, leading: length, bottom: length, trailing: length))
    }
    
    func htmlText2ContentPadding(_ edges: Edge.Set = .all, _ length: CGFloat) -> some View {
        let top: CGFloat = edges.contains(.top) ? length : 0
        let leading: CGFloat = edges.contains(.leading) ? length : 0
        let bottom: CGFloat = edges.contains(.bottom) ? length : 0
        let trailing: CGFloat = edges.contains(.trailing) ? length : 0
        return htmlText2ContentPadding(EdgeInsets(top: top, leading: leading, bottom: bottom, trailing: trailing))
    }
    
    func onHtmlText2ScrollOffsetChange(_ onChange: @escaping (CGPoint) -> Void) -> some View {
        environment(\.htmlText2ScrollOffset, onChange)
    }
}

struct HtmlText2 : UIViewRepresentable {
    
    @Environment(\.isScrollEnabled) private var isScrollEnabled
    @Environment(\.colorScheme) private var colorScheme

    @Environment(\.htmlText2ContentStyle) private var contentStyle
    @Environment(\.htmlText2ContentInsets) private var contentInsets
    @Environment(\.htmlText2ScrollOffset) private var scrollOffset
    
    let text: String
    
    init(_ text: String) {
        self.text = text
    }
    
    func makeUIView(context: Context) -> WKWebView {
        let webviewConfiguration = WKWebViewConfiguration()
        webviewConfiguration.defaultWebpagePreferences.allowsContentJavaScript = false
        
        let webView = WKWebView(frame: .null, configuration: webviewConfiguration)
        webView.isOpaque = false
        webView.backgroundColor = .clear
        webView.uiDelegate = context.coordinator
        webView.scrollView.zoomScale = 20
        webView.scrollView.delegate = context.coordinator
        
        return webView
    }
    
    func updateUIView(_ uiView: WKWebView, context: Context) {
        let primaryThemed = Color(uiColor: UIColor(Color.primary).resolvedColor(with: uiView.traitCollection))
        let contentStyleThemed = contentStyle ?? .init(
            h1: .init(color: primaryThemed, fontSize: 34, fontWeight: .regular),
            h2: .init(color: primaryThemed, fontSize: 30, fontWeight: .regular),
            h3: .init(color: primaryThemed, fontSize: 26, fontWeight: .regular),
            h4: .init(color: primaryThemed, fontSize: 23, fontWeight: .semibold),
            h5: .init(color: primaryThemed, fontSize: 21, fontWeight: .semibold),
            h6: .init(color: primaryThemed, fontSize: 19, fontWeight: .semibold),
            body: .init(color: primaryThemed, fontSize: 16, fontWeight: .regular),
            a: .init(color: .accentColor, fontSize: 16, fontWeight: .medium)
        )
        
        let html = """
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1, max-scale=1, user-scalable=no" />
            <style>
              :root {
                color-scheme: light dark;
              }
              
              \(contentStyleThemed.css)
        
              body {
                padding: \(contentInsets.css)
              }
              
              li {
                margin-inline-start: \(contentInsets.leading)px
              }
            </style>
          </head>
          <body>
            \(text)
          </body>
        </html>
        """
        uiView.loadHTMLString(html, baseURL: Bundle.main.bundleURL)
        uiView.scrollView.isScrollEnabled = isScrollEnabled
    }
    
    func makeCoordinator() -> HtmlText2Coordinator {
        return HtmlText2Coordinator {
            scrollOffset($0)
        }
    }
    
    class HtmlText2Coordinator: NSObject, WKUIDelegate, UIScrollViewDelegate {
        
        let onScrollChange: (CGPoint) -> Void
        
        private var lastScrollPosition: CGPoint = .zero
        private var shouldCorrectScroll = false
        private var isDragging = false
        
        init(onScrollChange: @escaping (CGPoint) -> Void) {
            self.onScrollChange = onScrollChange
        }
        
        func scrollViewDidScroll(_ scrollView: UIScrollView) {
//            if shouldCorrectScroll && !isDragging {
//                if lastScrollPosition.x > 0 && scrollView.contentOffset.x == 0 {
//                    scrollView.setContentOffset(lastScrollPosition, animated: false)
//                }
//                
//                if lastScrollPosition.y > 0 && scrollView.contentOffset.y == 0 {
//                    scrollView.setContentOffset(lastScrollPosition, animated: false)
//                }
//            }
            
//            if lastScrollPosition.x > 0 && scrollView.contentOffset.x == 0 {
//                scrollView.setContentOffset(lastScrollPosition, animated: false)
//            }
//            
//            if lastScrollPosition.y > 0 && scrollView.contentOffset.y == 0 {
//                scrollView.setContentOffset(lastScrollPosition, animated: false)
//            }
//            
//            lastScrollPosition = scrollView.contentOffset
            onScrollChange(scrollView.contentOffset)
        }
        
//        func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
//            isDragging = true
//        }
//        
//        func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
//            isDragging = false
//            shouldCorrectScroll = scrollView.contentOffset.y <= targetContentOffset.pointee.y
//        }
        
    }
}
