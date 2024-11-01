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

struct HtmlText2 : UIViewRepresentable {
    
    let html: String
    
    init(_ html: String) {
        self.html = html
    }
    
    func makeUIView(context: Context) -> WKWebView {
        let webviewConfiguration = WKWebViewConfiguration()
        webviewConfiguration.defaultWebpagePreferences.allowsContentJavaScript = false
        webviewConfiguration.userContentController.addUserScript(WKUserScript(source: "", injectionTime: .atDocumentStart, forMainFrameOnly: false))
        let webView = WKWebView(frame: .null, configuration: webviewConfiguration)
        webView.scrollView.zoomScale = 20
        webView.uiDelegate = context.coordinator
        return webView
    }
    
    func updateUIView(_ uiView: WKWebView, context: Context) {
        uiView.loadHTMLString(html, baseURL: Bundle.main.bundleURL)
    }
    
    func makeCoordinator() -> HtmlText2Coordinator {
        return HtmlText2Coordinator()
    }
    
    class HtmlText2Coordinator: NSObject, WKUIDelegate {
        
        
    }
    
}
