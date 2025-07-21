//
//  MessageScreen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 06.08.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class MessageViewModel {
    
    private var messageTask: Task<Void, Error>?
    
    private var messagesRepository: MessagesRepository {
        DiProvider.shared.messagesRepository
    }
    
    var state: MessageState = .loading
    
    init(messageId: String, semesterId: String) {
        messageTask = Task {
            for try await result in messagesRepository.getMessage(id: messageId, semId: semesterId).flow {
                self.state = switch onEnum(of: result) {
                    case .loading: .loading
                    case .success(let success): .success(message: success.value!)
                    case .error: .error
                }
            }
        }
    }
    
    deinit {
        messageTask?.cancel()
    }
}

struct MessageScreen: View {
    let messageId: String
    let semesterId: String
    
    @State private var viewModel: MessageViewModel
    
    init(messageId: String, semesterId: String) {
        self.messageId = messageId
        self.semesterId = semesterId
        self.viewModel = MessageViewModel(messageId: messageId, semesterId: semesterId)
    }
    
    var body: some View {
        _MessageScreen(state: viewModel.state)
    }
}

struct MessageScreenPreview: View {
    var body: some View {
        _MessageScreen(
            state: .success(
                message: DomainMessage(
                    id: "",
                    subject: "Test",
                    body: "Lorem ipsum",
                    semId: "",
                    source: DomainMessageSourceGeneral(
                        sender: DomainMessageUser(
                            fullName: "George Bush",
                            uid: ""
                        ),
                        receiver: DomainMessageUser(
                            fullName: "Donald Trump",
                            uid: ""
                        )
                    ),
                    sentAt: FormattedLocalDateTime(instant: Kotlinx_datetimeInstant.companion.DISTANT_FUTURE),
                    seenAt: nil
                )
            )
        )
    }
}

enum MessageState {
    case loading
    case success(message: DomainMessage)
    case error
}

struct _MessageScreen : View {
    
    let state: MessageState
    @State private var showTitle = false
 
    var body: some View {
        Group {
            switch state {
            case .loading:
                ProgressView()
            case .success(let message):
                HtmlText2(
                    """
                    <div style="display:flex; flex-direction:column; gap:8px;">
                        <h4>\(message.subject)</h4>
                        \(message.body)
                    </div>
                    """
                )
                .htmlText2ContentPadding(12)
                .onHtmlText2ScrollOffsetChange { offset in
                    withAnimation(.easeInOut(duration: 0.25)) {
                        showTitle = offset.y > 24
                    }
                }
//                .toolbar {
//                    ToolbarItem(placement: .principal) {
//                        Text(message.subject)
//                            .font(.headline)
//                            .opacity(showTitle ? 1 : 0)
//                            .transition(.opacity)
//                            .truncationMode(.tail)
//                    }
//                }
//                .navigationTitle(showTitle ? message.subject : "")
//                .navigationBarTitleDisplayMode(.inline)
            case .error:
                Text("Error")
            }
        }
        .toolbar {
            ToolbarItemGroup {
                Spacer()
                Button(action: /*@START_MENU_TOKEN@*/{}/*@END_MENU_TOKEN@*/) {
                    Label("Reply", systemImage: "arrowshape.turn.up.left")
                }
                Spacer()
                Button(role: .destructive, action: {}) {
                    Label("Delete", systemImage: "trash")
                }
                .tint(Color.red)
                Spacer()
            }
        }
    }
    
}

#Preview {
    MessageScreenPreview()
}
