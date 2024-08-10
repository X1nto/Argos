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
                case let .error(_):
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
        _UserSheet(
            settings: HStack {},
            studentInfo: StudentInfoScreen(),
            state: viewModel.state,
            onSignOut: viewModel.signOut
        )
    }
}

enum UserState {
    case loading
    case success(info: DomainMeUserInfo, state: DomainMeUserState)
    case error
}

struct UserSheetPreview: View {
    var body: some View {
        _UserSheet(
            settings: HStack {},
            studentInfo: StudentInfoScreenPreview(),
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
    }
}

struct _UserSheet<Settings: View, StudentInfo: View>: View {
    
    let settings: Settings
    let studentInfo: StudentInfo
    
    let state: UserState
    let onSignOut: () -> Void
    
    var body: some View {
        NavigationView {
            ZStack {
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
                                        .font(.system(size: 24))
                                        .fontWeight(.bold)
                                    Text(userInfo.email)
                                        .font(.system(size: 14))
                                        .foregroundStyle(.secondary)
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
                        Button(action: {}) {
                            Label {
                                HStack {
                                    Text("Balance")
                                    Spacer()
                                    
                                    Group {
                                        switch state {
                                        case .loading:
                                            ProgressView()
                                        case .success(_, let userState):
                                            Text(userState.billingBalance).foregroundStyle(.secondary)
                                        case .error:
                                            Image(systemName: "exclamationmark.circle").foregroundStyle(.red)
                                        }
                                    }
                                }
                            } icon: {
                                Image(systemName: "creditcard")
                                    .foregroundStyle(Color.green)
                            }
                        }
                        Button(action: {}) {
                            Label {
                                HStack {
                                    Text("Library")
                                    Spacer()
                                    
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
                    }.buttonStyle(.plain)
                    
                    Section {
                        NavigationLink(destination: studentInfo) {
                            Label("Student Information", systemImage: "person.text.rectangle")
                        }
                        NavigationLink(destination: EmptyView()) {
                            Label("Settings", systemImage: "gear")
                        }
                    }
                    
                    Section {
                        Button(action: onSignOut) {
                            Label("Sign out", systemImage: "rectangle.portrait.and.arrow.right")
                        }
                        .foregroundStyle(Color.red)
                    }
                }
            }
        }
    }
}

extension NavigationLink {
    init(destinaton: Destination, @ViewBuilder label: () -> Label) {
        self.init(destination: { destinaton }, label: label)
    }
}

#Preview {
    UserSheetPreview()
}

#Preview {
    _UserSheet(
        settings: EmptyView(),
        studentInfo: StudentInfoScreenPreview(),
        state: .loading,
        onSignOut: {}
    )
}

#Preview {
    _UserSheet(
        settings: EmptyView(),
        studentInfo: StudentInfoScreenPreview(),
        state: .error,
        onSignOut: {}
    )
}
