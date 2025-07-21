//
//  Models.swift
//  Argos
//
//  Created by Tornike Khintibidze on 06.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import ArgosCore

extension DomainMessage : @retroactive Identifiable {
    
}

extension DomainCourse : @retroactive Identifiable {
    
}

extension DomainMyCourse : @retroactive Identifiable {
    
}

extension DomainCourseGroup: @retroactive Identifiable {
    
}

extension DomainCourseGroupSchedule: @retroactive Identifiable {
    
}

extension DomainCourseClassmate: @retroactive Identifiable {
//    public var id: String {
//        uuid
//    }
}

extension DomainUserAuthorization: @retroactive Identifiable {
    
}
