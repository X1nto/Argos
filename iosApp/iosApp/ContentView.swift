import SwiftUI
import GoogleSignIn
import GoogleSignInSwift

struct ContentView: View {
    
    @State private var user: GIDGoogleUser? = nil
    @State private var error: String = ""
    
    
	var body: some View {
        VStack {
            if user != nil {
                Text(user!.profile!.email)
                Text(user!.idToken!.tokenString)
                
                Button(action: {
                    GIDSignIn.sharedInstance.signOut()
                    user = nil
                }) {
                    Text("Sign Out")
                }
            } else {
                Text(error)
                
                Button(action: {
                    guard let presentingViewController = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.rootViewController else {return}

                    GIDSignIn
                        .sharedInstance
                        .signIn(
                        withPresenting: presentingViewController
                    ) { result, error in
                        if let result = result {
                            user = result.user
                            print(user!.idToken!.tokenString)
                        } else {
                            self.error = error!.localizedDescription
                            return
                        }
                    }
                }) {
                    Text("Sign in")
                }
            }
        }
        .onOpenURL(perform: { url in
            GIDSignIn.sharedInstance.handle(url)
        })
        .onAppear {
            GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
                self.user = user
                if user != nil {
                    print(user!.idToken!.tokenString)
                }
            }
        }
	}
}

struct ContentViewController : UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> some UIViewController {
        let presentingViewController = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.rootViewController
        return presentingViewController!
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        
    }
    
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
        ContentViewController()
	}
}
