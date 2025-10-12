# CMS Publishing Workflows - Mermaid Diagrams

## 1. WordPress Publishing Workflow

```mermaid
flowchart TD
    A[Content Creation] --> B{Save as Draft}
    B --> C[Edit Draft]
    C --> D{Publish Immediately?}
    D -->|Yes| E[Published]
    D -->|No| F[Schedule Publication]
    F --> G[Scheduled]
    G --> H[Auto-publish at scheduled time]
    H --> E
    E --> I{Update Content?}
    I -->|Yes| J[Create New Draft]
    J --> K[Review Changes]
    K --> L{Publish Update?}
    L -->|Yes| E
    L -->|No| M[Keep as Draft]
```

## 2. Drupal Publishing Workflow

```mermaid
flowchart TD
    A[Content Creation] --> B[Draft State]
    B --> C{Submit for Review?}
    C -->|Yes| D[Review State]
    C -->|No| E[Edit Draft]
    E --> B
    D --> F[Reviewer Actions]
    F --> G{Approve Content?}
    G -->|Yes| H[Approved State]
    G -->|No| I[Needs Work]
    I --> E
    H --> J{Publish Content?}
    J -->|Yes| K[Published State]
    J -->|No| L[Keep Approved]
    K --> M{Content Update?}
    M -->|Yes| N[New Draft Version]
    N --> O[Revision Process]
    O --> K
```

## 3. Adobe Experience Manager (AEM) Publishing Workflow

```mermaid
flowchart TD
    A[Content Creation] --> B[Draft State]
    B --> C[Inbox Notification]
    C --> D[Content Review]
    D --> E{Approval Required?}
    E -->|Yes| F[Multi-level Approval]
    F --> G[Approver 1]
    G --> H{Approve?}
    H -->|Yes| I[Approver 2]
    H -->|No| J[Request Changes]
    J --> K[Content Revision]
    K --> D
    I --> L{Final Approval?}
    L -->|Yes| M[Approved]
    L -->|No| J
    M --> N{Publish to Production?}
    N -->|Yes| O[Production Publish]
    N -->|No| P[Scheduled Publish]
    P --> Q[Auto-publish at scheduled time]
    Q --> O
```

## 4. Sitecore Publishing Workflow

```mermaid
flowchart TD
    A[Content Authoring] --> B[Draft State]
    B --> C{Submit for Approval?}
    C -->|Yes| D[Approval Workflow]
    C -->|No| E[Continue Editing]
    E --> B
    D --> F[First-Level Review]
    F --> G{Approve?}
    G -->|Yes| H[Second-Level Review]
    G -->|No| I[Request Revisions]
    I --> J[Content Updates]
    J --> B
    H --> K{Final Approval?}
    K -->|Yes| L[Approved State]
    K -->|No| I
    L --> M{Publish Immediately?}
    M -->|Yes| N[Live Publish]
    M -->|No| O[Scheduled Publish]
    O --> P[Auto-publish at scheduled time]
    P --> N
    N --> Q{Personalization?}
    Q -->|Yes| R[Apply Personalization Rules]
    Q -->|No| S[Standard Content]
```

## 5. Contentful Publishing Workflow

```mermaid
flowchart TD
    A[Content Modeling] --> B[Content Creation]
    B --> C[Draft State]
    C --> D{Validate Content?}
    D -->|Yes| E[Validation Process]
    D -->|No| F[Direct to Publish]
    E --> G{Content Valid?}
    G -->|Yes| H[Ready to Publish]
    G -->|No| I[Content Corrections]
    I --> C
    H --> J{Publish Content?}
    J -->|Yes| K[API Publish]
    J -->|No| L[Schedule Publish]
    L --> M[Scheduled via API]
    M --> K
    K --> N[Content Delivery API]
    N --> O[Multiple Platforms]
    O --> P[Web Applications]
    O --> Q[Mobile Apps]
    O --> R[IoT Devices]
```

## 6. Strapi Publishing Workflow

```mermaid
flowchart TD
    A[Content Modeling] --> B[Content Creation]
    B --> C[Draft State]
    C --> D{Validate Content?}
    D -->|Yes| E[Validation Process]
    D -->|No| F[Direct to Publish]
    E --> G{Content Valid?}
    G -->|Yes| H[Ready to Publish]
    G -->|No| I[Content Corrections]
    I --> C
    H --> J{Publish Content?}
    J -->|Yes| K[API Publish]
    J -->|No| L[Schedule Publish]
    L --> M[Scheduled via API]
    M --> K
    K --> N[Content Delivery API]
    N --> O[Multiple Platforms]
    O --> P[Web Applications]
    O --> Q[Mobile Apps]
    O --> R[Custom Applications]
```

## 7. Decap CMS Publishing Workflow

```mermaid
flowchart TD
    A[Content Editing] --> B{Save Changes}
    B --> C[Local Draft]
    C --> D{Submit for Review?}
    D -->|Yes| E[Create Pull Request]
    D -->|No| F[Continue Editing]
    F --> C
    E --> G[Pull Request Review]
    G --> H{Approve Changes?}
    H -->|Yes| I[Merge to Main Branch]
    H -->|No| J[Request Changes]
    J --> K[Update Pull Request]
    K --> G
    I --> L[Git Commit]
    L --> M[CI/CD Pipeline]
    M --> N{Deploy to Production?}
    N -->|Yes| O[Production Deployment]
    N -->|No| P[Staging Deployment]
```

## 8. Deployment Model Workflows

### Self-Hosted Deployment Workflow

```mermaid
flowchart TD
    A[Development] --> B{Test Locally}
    B -->|Fail| C[Fix Issues]
    C --> B
    B -->|Pass| D[Commit to Repository]
    D --> E[CI/CD Pipeline]
    E --> F{Automated Tests}
    F -->|Fail| G[Fix Issues]
    G --> D
    F -->|Pass| H[Deploy to Staging]
    H --> I{Staging Tests}
    I -->|Fail| J[Fix Issues]
    J --> D
    I -->|Pass| K[Deploy to Production]
    K --> L[Monitor Production]
    L --> M{Issues Detected?}
    M -->|Yes| N[Rollback]
    N --> O[Fix Issues]
    O --> D
    M -->|No| P[Continue Operations]
```

### SaaS Deployment Workflow

```mermaid
flowchart TD
    A[Content Creation] --> B[Use CMS Interface]
    B --> C{Validate Content}
    C -->|Fail| D[Fix Content]
    D --> B
    C -->|Pass| E[Save Content]
    E --> F{Publish Immediately?}
    F -->|Yes| G[Publish via API]
    F -->|No| H[Schedule Publication]
    H --> I[Auto-publish at scheduled time]
    I --> G
    G --> J[Monitor Performance]
    J --> K{Issues Detected?}
    K -->|Yes| L[Contact Support]
    K -->|No| M[Continue Operations]
```

### Git-Based Deployment Workflow

```mermaid
flowchart TD
    A[Content Editing] --> B{Save Changes}
    B --> C[Local Changes]
    C --> D{Commit Changes?}
    D -->|No| E[Continue Editing]
    E --> C
    D -->|Yes| F[Git Commit]
    F --> G{Push to Remote?}
    G -->|No| H[Continue Local Work]
    H --> C
    G -->|Yes| I[Push to Remote Repository]
    I --> J[CI/CD Pipeline Triggered]
    J --> K{Automated Tests}
    K -->|Fail| L[Fix Issues]
    L --> F
    K -->|Pass| M[Create Pull Request]
    M --> N[Review Process]
    N --> O{Approve Changes?}
    O -->|No| P[Request Changes]
    P --> L
    O -->|Yes| Q[Merge to Main Branch]
    Q --> R[Automatic Deployment]
    R --> S[Content Live]
```

## Workflow Comparison Summary

Each CMS platform has a distinct approach to content publishing workflows:

1. **WordPress**: Simple linear workflow, best extended with plugins for enterprise use
2. **Drupal**: State-based workflow with customizable transitions and robust permissions
3. **AEM**: Visual workflow designer with multi-level approvals and creative integration
4. **Sitecore**: Marketing-focused workflow with personalization integration
5. **Contentful**: API-first approach with external workflow tool integration
6. **Strapi**: API-first approach similar to Contentful with customization through code
7. **Decap CMS**: Git-based workflow with pull request review process

Additionally, deployment models significantly impact how content moves through systems:

1. **Self-Hosted**: Requires comprehensive deployment and testing workflows
2. **SaaS**: Simplified workflow with vendor-managed infrastructure
3. **Git-Based**: Integrated content and deployment workflow through version control

These diagrams illustrate the core workflow patterns of each platform, showing how content moves from creation to publication, including review and approval processes where applicable. The deployment model workflows show how infrastructure considerations affect content publishing processes.