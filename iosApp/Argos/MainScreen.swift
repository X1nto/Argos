//
//  MainScreen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 22.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore

@Observable
class MainViewModel {
    private var userTask: Task<Void, Error>?
    
    private var userRepository: UserRepository {
        DiProvider.shared.userRepository
    }
    
    var state: MainState = .loading
    
    init() {
        userTask = Task {
            for try await data in userRepository.meUserInfoAndStateFlow {
                self.state = switch onEnum(of: data) {
                case let .success(userInfoAndState):
                    .success(
                        userInfo: userInfoAndState.value!.first!,
                        userState: userInfoAndState.value!.second!
                    )
                case .loading: .loading
                case .error(_): .error
                }
            }
        }
    }
    
    deinit {
        userTask?.cancel()
    }
}

struct MainScreen: View {
    
    @State private var viewModel = MainViewModel()
    
    var body: some View {
        _MainScreen(
            homeScreen: {
                ArgosTab {
                    HomeScreen()
                }
            },
            messagesScreen: {
                ArgosTab {
                    MessagesScreen()
                }
            },
            courseCatalogScreen: {
                ArgosTab {
                    CourseCatalogScreen()
                }
            },
            newsScreen: {
                ArgosTab {
                    NewsListScreen()
                }
            },
            state: viewModel.state
        )
    }
}

enum MainState {
    case loading
    case success(userInfo: DomainMeUserInfo, userState: DomainMeUserState)
    case error
    
    static let mockSuccess: Self = .success(
        userInfo: DomainMeUserInfo(
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
        userState: DomainMeUserState(
            billingBalance: "0.00", 
            libraryBalance: "0",
            newsUnread: 100,
            messagesUnread: 7,
            notificationsUnread: 1000
        )
    )
    
}

struct MainScreenPreview: View {
    var body: some View {
        _MainScreen(
            homeScreen: { HomeScreen() },
            messagesScreen: { MessagesScreenPreview() },
            courseCatalogScreen: { EmptyView() },
            newsScreen: { HomeScreen() },
            state: .mockSuccess
        )
    }
}

private struct _MainScreen<HomeScreen: View, MessagesScreen: View, CourseCatalogScreen: View, NewsScreen: View> : View {
    
    let homeScreen: () -> HomeScreen
    let messagesScreen: () -> MessagesScreen
    let courseCatalogScreen: () -> CourseCatalogScreen
    let newsScreen: () -> NewsScreen
    
    let state: MainState
    
    @State private var userSheetVisible = false
    @State private var selectedTab = "Home"
    
    private var messagesUnreadCount: Int {
        if case let .success(_, state) = state {
            return Int(state.messagesUnread)
        }
        return 0
    }
    
    private var newsUnreadCount: Int {
        if case let .success(_, state) = state {
            return Int(state.newsUnread)
        }
        return 0
    }
    
    var body: some View {
        TabView(selection: $selectedTab) {
            homeScreen()
                .tabItem {
                    Label("Home", systemImage: "house")
                }
                .tag("Home")
            
            messagesScreen()
                .tabItem {
                    Label("Messages", systemImage: "mail")
                }
                .tag("Messages")
                .badge(messagesUnreadCount)
            
            courseCatalogScreen()
                .tabItem {
                    Label("Courses", systemImage: "graduationcap")
                }
                .tag("Courses")
            
            newsScreen()
                .tabItem {
                    Label("News", systemImage: "newspaper")
                }
                .tag("News")
                .badge(newsUnreadCount)

        }
    }
}

#Preview {
    MainScreenPreview()
}
