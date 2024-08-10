//
//  PersonalInfoScreen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 28.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore

@Observable
class StudentInfoViewModel {
    
    private var userInfoTask: Task<Void, Error>?
    private var userRepository: UserRepository {
        DiProvider.shared.userRepository
    }
    
    var state: StudentInfoState = .loading
    
    init() {
        userInfoTask = Task {
            for try await meUserInfo in userRepository.meUserInfo.flow {
                state = switch onEnum(of: meUserInfo) {
                    case .loading: .loading
                    case let .success(userInfo): .success(userInfo.value!)
                    case .error: .error
                }
            }
        }
    }
    
    deinit {
        userInfoTask?.cancel()
    }
    
}

struct StudentInfoScreen: View {
    
    @State private var viewModel = StudentInfoViewModel()
    
    var body: some View {
        _PersonalInfoScreen(state: viewModel.state)
    }
}

struct StudentInfoScreenPreview: View {
    var body: some View {
        _PersonalInfoScreen(state: .success(
            DomainMeUserInfo(
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
            )
        ))
    }
}

enum StudentInfoState {
    case loading
    case success(DomainMeUserInfo)
    case error
}

struct _PersonalInfoScreen: View {
    
    let state: StudentInfoState
    
    private let contactInfo: StudentContactInfo
    @State private var changedContactInfo: StudentContactInfo
    
    init(state: StudentInfoState) {
        self.state = state
        
        contactInfo = if case let .success(domainMeUserInfo) = state {
            StudentContactInfo(
                currentAddress: domainMeUserInfo.currentAddress,
                mobile1: domainMeUserInfo.mobileNumber1,
                mobile2: domainMeUserInfo.mobileNumber2,
                phone1: domainMeUserInfo.homeNumber
            )
        } else {
            StudentContactInfo(
                currentAddress: "",
                mobile1: "",
                mobile2: "",
                phone1: ""
            )
        }
        changedContactInfo = contactInfo
    }
    
    var body: some View {
        Group {
            switch state {
            case .loading:
                ProgressView()
            case let .success(userInfo):
                Form {
                    Section("Personal information") {
                        Group {
                            TextField(text: .constant(userInfo.firstName)) {
                                Text("First name")
                            }
                            TextField(text: .constant(userInfo.lastName)) {
                                Text("Last name")
                            }
                            TextField(text: .constant(userInfo.birthDate)) {
                                Text("Birth date")
                            }
                            TextField(text: .constant(userInfo.idNumber)) {
                                Text("Personal ID")
                            }
                            TextField(text: .constant(userInfo.juridicalAddress)) {
                                Text("Juridical address")
                            }
                        }
                        .foregroundStyle(Color.gray)
                        .fontWeight(.medium)
                        .disabled(true)
                    }
                    
                    Section("Contact information") {
                        TextField(text: .constant(userInfo.email)) {
                            Text("Email")
                        }
                        .foregroundStyle(Color.gray)
                        .fontWeight(.medium)
                        .disabled(true)
                        
                        TextField(text: $changedContactInfo.mobile1) {
                            Text("Mobile 1")
                        }
                        TextField(text: $changedContactInfo.mobile2) {
                            Text("Mobile 2")
                        }
                        TextField(text: $changedContactInfo.phone1) {
                            Text("Phone 1")
                        }
                        TextField(text: $changedContactInfo.currentAddress) {
                            Text("Current address")
                        }
                    }
                }
            case .error:
                Text("Error")
            }
        }
        .navigationTitle("Student Information")
        .toolbar {
            Button(action: {}) {
                Text("Save")
            }
            .disabled(changedContactInfo == contactInfo)
        }
    }
}

struct StudentContactInfo: Equatable {
    var currentAddress: String
    var mobile1: String
    var mobile2: String
    var phone1: String
}

#Preview {
    StudentInfoScreenPreview()
}
