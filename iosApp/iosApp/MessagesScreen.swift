//
//  MessagesScreen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class MessagesViewModel {
    @ObservationIgnored private var semesterTask: Task<Void, Error>?
    
    private var messagesRepository: MessagesRepository {
        DiProvider.shared.messagesRepository
    }
    private var semesterRepository: SemesterRepository {
        DiProvider.shared.semesterRepository
    }
    
    private(set) var receivedMessages: PagingItemsObservable<DomainMessageReceived>?
    private(set) var sentMessages: PagingItemsObservable<DomainMessageSent>?
    private(set) var state: MessagesState = .loading
    
    var activeSemester: DomainSemester? = nil {
        didSet {
            guard let semester = activeSemester else { return }
            receivedMessages = PagingItemsObservable(messagesRepository.getInboxMessages(semesterId: semester.id))
            sentMessages = PagingItemsObservable(messagesRepository.getOutboxMessages(semesterId: semester.id))
        }
    }
    
    init() {
        semesterTask = Task {
            for try await semesterResponse in semesterRepository.semesters.flow {
                switch onEnum(of: semesterResponse) {
                case .loading: 
                    state = .loading
                case let .success(data):
                    let semesters = data.value! as! [DomainSemester]
                    activeSemester = semesters.first { $0.active }
                    state = .success(semesters: semesters)
                case .error:
                    state = .error
                }
            }
        }
    }
    
    deinit {
        semesterTask?.cancel()
    }
}

struct MessagesScreen: View {
    
    @State private var viewModel = MessagesViewModel()
    
    var body: some View {
        _MessagesScreen(
            activeSemester: $viewModel.activeSemester,
            state: viewModel.state,
            receivedMessages: viewModel.receivedMessages,
            sentMessages: viewModel.sentMessages
        )
    }
}


enum MessagesState {
    case loading
    case success(semesters: [DomainSemester])
    case error
}

struct MessagesScreenPreview: View {
    
    private let receivedMessages = Paging_commonPagingDataCompanion.shared.from(
        data: [
            DomainMessageReceived(
                id: "",
                subject: "",
                semId: "",
                date: "",
                sender: DomainMessageUser(
                    fullName: "",
                    uid: ""
                ),
                seen: true
            )
        ]
    )
    private let sentMessages = Paging_commonPagingDataCompanion.shared.from(
        data: [
            DomainMessageSent(
                id: "",
                subject: "",
                semId: "",
                date: "",
                receiver: DomainMessageUser(
                    fullName: "",
                    uid: ""
                )
            )
        ]
    )
    
    var body: some View {
        _MessagesScreen(
            activeSemester: .constant(nil),
            state: .success(semesters: []),
            receivedMessages: nil,
            sentMessages: nil
        )
    }
}

private enum MessagesPage {
    case received
    case sent
}

private struct _MessagesScreen: View {
    
    @Binding var activeSemester: DomainSemester?
    @State var selectedPage = MessagesPage.received
    let state: MessagesState
    let receivedMessages: PagingItemsObservable<DomainMessageReceived>?
    let sentMessages: PagingItemsObservable<DomainMessageSent>?
    
    init(
        activeSemester: Binding<DomainSemester?> = .constant(nil),
        state: MessagesState,
        selectedPage: MessagesPage = MessagesPage.received,
        receivedMessages: PagingItemsObservable<DomainMessageReceived>? = nil, 
        sentMessages: PagingItemsObservable<DomainMessageSent>? = nil
    ) {
        self._activeSemester = activeSemester
        self.state = state
        self.selectedPage = selectedPage
        self.receivedMessages = receivedMessages
        self.sentMessages = sentMessages
    }
    
    var body: some View {
        VStack {
            VStack {
                if case let .success(semesters) = state {
                    HStack {
                        SearchField(
                            text: .constant(""),
                            placeholder: "Search messages"
                        )
                        .padding(.horizontal, -8)
                        .padding(.vertical, -10)
                         
                        Menu {
                            Picker(selection: $activeSemester, label: EmptyView()) {
                                ForEach(semesters, id: \.self) { semester in
                                    Text(semester.name)
                                        .tag(DomainSemester?.some(semester))
                                }
                            }
                            .labelStyle(.iconOnly)
                        } label: {
                            Image(systemName: "calendar")
                        }
                    }
                }
                
                HStack {
                    Picker("Page", selection: $selectedPage) {
                        Text("Inbox").tag(MessagesPage.received)
                        Text("Outbox").tag(MessagesPage.sent)
                    }
                    .pickerStyle(.segmented)
                }
            }
            .padding(.horizontal, 16)
            
            Spacer()
            
            switch selectedPage {
            case .received:
                if let messages = receivedMessages {
                    List {
                        ForEach(0..<messages.items.count, id: \.self) { index in
                            let message = messages[index]
                            NavigationLink(
                                destination: MessageScreen(
                                    messageId: message.id,
                                    semesterId: message.semId
                                )
                            ) {
                                Message(message)
                            }
                        }
                    }
                }
            case .sent:
                if let messages = sentMessages {
                    List {
                        ForEach(0..<messages.items.count, id: \.self) { index in
                            let message = messages[index]
                            NavigationLink(
                                destination: MessageScreen(
                                    messageId: message.id,
                                    semesterId: message.semId
                                )
                            ) {
                                Message(message)
                            }
                        }
                    }
                }
            }
        }
        .listStyle(.inset)
    }
}

private struct Message : View {
    let message: DomainMessagePreview
    
    init(_ message: DomainMessagePreview) {
        self.message = message
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(message.user.fullName)
                .font(.system(size: 16))
                .fontWeight(.medium)
            Text(message.subject)
                .font(.system(size: 14))
        }
    }
}



#Preview {
    MessagesScreenPreview()
}
