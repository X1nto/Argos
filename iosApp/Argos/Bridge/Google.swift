//
//  Google.swift
//  Argos
//
//  Created by Tornike Khintibidze on 06.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import GoogleSignIn

extension GIDSignIn {
    
    func signIn(withPresenting presentingViewController: UIViewController) async throws -> GIDSignInResult {
        try await withCheckedThrowingContinuation { continuation in
            signIn(withPresenting: presentingViewController) { result, error in
                if let error {
                    continuation.resume(throwing: error)
                    return
                }
                
                continuation.resume(returning: result!)
            }
        }
    }
    
}
