//
//  UserSheet.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 27.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore

@Observable
class UserViewModel {
    
    private var userTask: Task<Void, Error>?
    
    private var userRepository: UserRepository {
        DiProvider.shared.userRepository
    }
    
    var state: UserState = .loading
    
    init() {
        userTask = Task {
            for try await meUserInfoAndState in userRepository.meUserInfoAndStateFlow {
                switch onEnum(of: meUserInfoAndState) {
                case .loading:
                    state = .loading
                case let .success(userData):
                    state = .success(info: userData.value!.first!, state: userData.value!.second!)
                case .error:
                    state = .error
                }
            }
        }
    }
    
    func signOut() {
        userRepository.logout()
    }
    
    deinit {
        userTask?.cancel()
    }
}

struct UserSheet: View {
    
    @State private var viewModel = UserViewModel()
    
    var body: some View {
        NavigationStack {
            ZStack {
                _UserSheet(
                    state: viewModel.state,
                    onSignOut: viewModel.signOut
                )
                .navigationDestination(for: String.self) { s in
                    switch s {
                    case "studentinfo":
                        StudentInfoScreen()
                    case "authorizations":
                        AuthorizationsScreen()
                    default:
                        EmptyView()
                    }
                }
            }
        }
    }
}

enum UserState {
    case loading
    case success(info: DomainMeUserInfo, state: DomainMeUserState)
    case error
}

struct UserSheetPreview: View {
    var body: some View {
        NavigationStack {
            ZStack {
                _UserSheet(
                    state: UserState.success(
                        info: DomainMeUserInfo(
                            firstName: "Donald",
                            lastName: "Trump",
                            fullName: "Donald Trump",
                            birthDate: "19/09/1999",
                            idNumber: "01011234567",
                            email: "donald.trump.1@iliauni.edu.ge",
                            mobileNumber1: "+995555555555",
                            mobileNumber2: "",
                            homeNumber: "",
                            juridicalAddress: "WA, White House",
                            currentAddress: "WA, White House",
                            photoUrl: "https://upload.wikimedia.org/wikipedia/en/c/c5/Donald_Trump_mug_shot.jpg",
                            degree: 1
                        ),
                        state: DomainMeUserState(
                            billingBalance: "0",
                            libraryBalance: "0",
                            newsUnread: 0,
                            messagesUnread: 0,
                            notificationsUnread: 0
                        )
                    ),
                    onSignOut: {}
                )
                .navigationDestination(for: String.self) { s in
                    if s == "studentinfo" {
                        StudentInfoScreenPreview()
                    } else {
                        EmptyView()
                    }
                }
            }
        }
    }
}

struct _UserSheet: View {
    
    let state: UserState
    let onSignOut: () -> Void
    
    @State private var showEmailCopy = false
    
    var body: some View {
        List {
            Section {
                VStack(alignment: .center) {
                    switch state {
                    case .loading:
                        ProgressView()
                    case .success(let userInfo, _):
                        if let photoUrl = userInfo.photoUrl {
                            AsyncImage(url: URL(string: photoUrl)) { image in
                                image
                                    .resizable()
                                    .scaledToFill()
                                    .clipShape(Circle())
                                    .frame(maxWidth: 92, maxHeight: 92)
                            } placeholder: {
                                ProgressView()
                            }
                        } else {
                            Image(systemName: "person.crop.circle")
                                .resizable()
                                .scaledToFit()
                                .frame(maxWidth: 60, maxHeight: 60)
                        }
                        
                        VStack(spacing: 4) {
                            Text(userInfo.fullName)
                                .font(.title2)
                                .fontWeight(.bold)
                            
                            Button(action: { showEmailCopy = true }) {
                                Text(userInfo.email)
                                    .font(.system(size: 14))
                                    .foregroundStyle(.secondary)
                                    .popover(isPresented: $showEmailCopy, attachmentAnchor: .point(.top), arrowEdge: .bottom) {
                                        Button(action: {
                                            UIPasteboard.general.string = userInfo.email
                                            showEmailCopy = false
                                        }) {
                                            Text("Copy")
                                        }
                                        .presentationCompactAdaptation(.popover)
                                    }
                            }
                            .buttonStyle(.plain)
                        }
                    case .error:
                        Image(systemName: "person.crop.circle.badge.exclamationmark")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 60, height: 60)
                            .foregroundStyle(Color.red)
                    }
                }
                .frame(maxWidth: .infinity)
            }
            .listRowInsets(EdgeInsets())
            .listRowBackground(Color.clear)
            
            Section {
                Label {
                    LabeledContent("Balance") {
                        switch state {
                        case .loading:
                            ProgressView()
                        case .success(_, let userState):
                            Text(userState.billingBalance).foregroundStyle(.secondary)
                        case .error:
                            Image(systemName: "exclamationmark.circle").foregroundStyle(.red)
                        }
                    }
                } icon: {
                    Image(systemName: "creditcard")
                        .foregroundStyle(Color.green)
                }
                Label {
                    LabeledContent("Library") {
                        switch state {
                        case .loading:
                            ProgressView()
                        case .success(_, let userState):
                            Text(userState.libraryBalance).foregroundStyle(.secondary)
                        case .error:
                            Image(systemName: "exclamationmark.circle").foregroundStyle(.red)
                        }
                    }
                } icon: {
                    Image(systemName: "book.closed")
                        .foregroundStyle(Color.yellow)
                }
            }
            
            Section {
                NavigationLink(value: "authorizations") {
                    Label("Authorizations", systemImage: "light.beacon.min")
                }
                NavigationLink(value: "studentinfo") {
                    Label("Student Information", systemImage: "person.text.rectangle")
                }
                NavigationLink(value: "settings") {
                    Label("Settings", systemImage: "gear")
                }
            }
            
            Section {
                Button(role: .destructive, action: onSignOut, ) {
                    Label("Sign out", systemImage: "rectangle.portrait.and.arrow.right")
                }
                .foregroundStyle(Color.red)
            }
        }
    }
}

#Preview {
    UserSheetPreview()
}

#Preview {
    _UserSheet(
        state: .loading,
        onSignOut: {}
    )
}

#Preview {
    _UserSheet(
        state: .error,
        onSignOut: {}
    )
}
