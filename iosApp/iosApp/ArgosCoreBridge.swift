//
//  ArgosCoreBridge.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ArgosCore

extension DomainMessage : Identifiable {
    
}

extension DomainMessageSent : Identifiable {
    
}

extension DomainMessageReceived : Identifiable {
    
}

typealias PagingData<T: AnyObject> = Paging_commonPagingData<T>
let PagingDataCompanion = Paging_commonPagingDataCompanion.shared
