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
    private(set) var receivedMessages: PagingItemsObservable<DomainMessage>?
    private(set) var sentMessages: PagingItemsObservable<DomainMessage>?
    private(set) var state: MessagesState = .loading
    
    var activeSemester: DomainSemester? = nil {
        didSet {
            guard let semester = activeSemester else { return }
            receivedMessages = PagingItemsObservable(MessagesRepository.shared.getInboxMessages(semesterId: semester.id))
            sentMessages = PagingItemsObservable(MessagesRepository.shared.getOutboxMessages(semesterId: semester.id))
        }
    }
    
    init() {
        Task {
            for try await response in SemesterRepository.shared.semesters.flow {
                switch onEnum(of: response) {
                case .loading:
                    await MainActor.run {
                        state = .loading
                    }
                case let .success(data):
                    await MainActor.run {
                        let semesters = data.value! as! [DomainSemester]
                        activeSemester = semesters.first { $0.active }
                        state = .success(semesters: semesters)
                    }
                case .error:
                    await MainActor.run {
                        state = .error
                    }
                }
            }
        }
    }
    
    //TODO refresh semesters too
    func refresh() {
        receivedMessages?.refresh()
        sentMessages?.refresh()
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
        .navigationDestination(for: DomainMessage.self) { message in
            MessageScreen(
                messageId: message.id,
                semesterId: message.semId
            )
        }
        .refreshable {
            viewModel.refresh()
        }
    }
}


enum MessagesState {
    case loading
    case success(semesters: [DomainSemester])
    case error
}

struct MessagesScreenPreview: View {
    
    private let receivedMessages = Paging_commonPagingDataCompanion.shared.from(
        data: []
    )
    private let sentMessages = Paging_commonPagingDataCompanion.shared.from(
        data: []
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
    let receivedMessages: PagingItemsObservable<DomainMessage>?
    let sentMessages: PagingItemsObservable<DomainMessage>?
    
    init(
        activeSemester: Binding<DomainSemester?> = .constant(nil),
        state: MessagesState,
        selectedPage: MessagesPage = MessagesPage.received,
        receivedMessages: PagingItemsObservable<DomainMessage>? = nil,
        sentMessages: PagingItemsObservable<DomainMessage>? = nil
    ) {
        self._activeSemester = activeSemester
        self.state = state
        self.selectedPage = selectedPage
        self.receivedMessages = receivedMessages
        self.sentMessages = sentMessages
    }
    
    var body: some View {
        List {
            switch selectedPage {
            case .received:
                if let messages = receivedMessages {
                    ForEach(messages) { message in
                        MessagePreview(message)
                    }
                }
            case .sent:
                if let messages = sentMessages {
                    ForEach(messages) { message in
                        MessagePreview(message)
                    }
                }
            }
        }
        .listStyle(.inset)
        .navigationTitle("Messages")
        .toolbarTitleDisplayMode(.inlineLarge)
        .environment(\.defaultMinListHeaderHeight, 0)
        .searchable(text: .constant(""))
        .toolbar {
            ToolbarItem(placement: .bottomBar) {
                HStack {
                    Picker("Page", selection: $selectedPage) {
                        Text("Inbox").tag(MessagesPage.received)
                        Text("Outbox").tag(MessagesPage.sent)
                    }
                    .pickerStyle(.segmented)
                    
                    if case let .success(semesters) = state {
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
                .padding(.bottom, 8)
            }
        }
    }
}

private struct MessagePreview : View {
    let message: DomainMessage
    
    init(_ message: DomainMessage) {
        self.message = message
    }
    
    private var user: DomainMessageUser {
        switch onEnum(of: message.source) {
        case let .inbox(inbox):
            inbox.sender
        case let .outbox(outbox):
            outbox.receiver
        case let .general(general):
            general.sender
        }
    }
    
    private var sanitizedBody: String {
        message.body
            .replacingOccurrences(of: "<br />", with: "")
            .replacingOccurrences(of: "\n", with: "")
    }
    
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            PrettyAvatarView(name: user.fullName)
            
            VStack(alignment: .leading) {
                HStack(spacing: 4) {
                    Text(user.fullName)
                        .font(.headline)
                    Spacer()
                    Text(message.sentAt.relativeDateTime)
                        .font(.caption)
                        .foregroundStyle(Color.secondary)
                    Image(systemName: "chevron.right")
                        .font(.caption)
                        .foregroundStyle(Color.secondary)
                }
                Text(message.subject)
                    .font(.subheadline)
                    .lineLimit(1)
                Text(sanitizedBody)
                    .font(.subheadline)
                    .lineLimit(2)
                    .foregroundStyle(Color.secondary)
            }
        }
        .overlay(NavigationLink("", value: message).opacity(0))
        .contextMenu {
            if case .outbox = onEnum(of: message.source), message.seenAt != nil {
                Button(action: {}) {
                    Text("Seen \(message.seenAt!.fullDateTime)")
                }
                .disabled(true)
                
                Divider()
            }
            
            Button("Sender profile", systemImage: "person.crop.circle") {
                
            }
            Button("Reply", systemImage: "arrowshape.turn.up.left.fill") {
                
            }
            Button("Delete", systemImage: "trash.fill", role: .destructive) {
                
            }
        } preview: {
            HtmlText2(message.body)
                .htmlText2ContentPadding(16)
        }
        .alignmentGuide(.listRowSeparatorLeading) { _ in
            52 // 40 for the avatar icon + 8 for spacing
        }
    }
}

#Preview {
    MessagesScreenPreview()
}
