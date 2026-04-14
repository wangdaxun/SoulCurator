# SoulCurator Project Details

## Project Vision
**核心价值**: "不是推荐你看什么，而是帮你发现**你想成为谁**"

## 7 Core Values
1. **产品有灵魂**: 情感连接、成长轨迹、审美教育、自我发现
2. **填补真实空白**: 技术深度 + 文艺深度
3. **对抗算法茧房**: 推荐成长路径，注重情感共鸣
4. **证明"慢产品"价值**: 在快时代追求深度
5. **INTP的浪漫**: 用系统思维解构情感，用逻辑架构承载审美
6. **社会价值**: 审美普惠、情感教育、思考训练
7. **最终愿景**: 让每一次文艺消费都成为一次自我发现的旅程

## Technical Architecture

### Backend (Spring Boot)
- Framework: Spring Boot 3.x
- Java: JDK 17+
- Database: PostgreSQL
- API: RESTful + GraphQL (optional)
- Security: JWT + OAuth2

### Frontend (Vue3)
- Framework: Vue 3 + Composition API
- Language: TypeScript
- Build: Vite
- UI Library: Element Plus / Ant Design Vue
- State Management: Pinia

### AI Integration
- Protocol: MCP (Model Context Protocol)
- Workflow: n8n for automation
- Models: OpenAI, Claude, local LLMs
- Vector DB: Pinecone / Weaviate (optional)

### Deployment
- Container: Docker
- Orchestration: Docker Compose / Kubernetes
- CI/CD: GitHub Actions
- Monitoring: Prometheus + Grafana

## Project Timeline (30 Days)

### Phase 1: Planning & Design (Days 1-5)
- [x] Day 1-2: Project ideation & manifesto
- [x] Day 3-4: Technical architecture design
- [x] Day 5: Detailed design documents

### Phase 2: Core Development (Days 6-15)
- [ ] Day 6-8: User authentication module
- [ ] Day 9-11: Content management system
- [ ] Day 12-14: Recommendation algorithm prototype
- [ ] Day 15: MVP integration

### Phase 3: Feature Enhancement (Days 16-25)
- [ ] Day 16-18: AI integration & MCP servers
- [ ] Day 19-21: UI/UX refinement
- [ ] Day 22-24: Testing & bug fixes
- [ ] Day 25: Performance optimization

### Phase 4: Polish & Launch (Days 26-30)
- [ ] Day 26-27: Documentation & tutorials
- [ ] Day 28-29: Deployment & monitoring
- [ ] Day 30: Launch & post-launch plan

## Key Milestones
1. **MVP Ready**: Day 15 (2026-04-12)
2. **Feature Complete**: Day 25 (2026-04-22)
3. **Launch**: Day 30 (2026-04-27)

## File Structure

### Workspace
```
/Users/wangdaxun/.openclaw/workspace/
├── MEMORY.md                    # Long-term memory
├── memory/YYYY-MM-DD.md         # Daily logs
├── soul-curator-manifesto.md    # Project vision
├── soul-curator-architecture.md # Tech design
└── soul-curator-progress-tracker.md # Progress tracking
```

### Project Code
```
/Users/wangdaxun/SoulCurator/
├── soul-curator-frontend/       # Vue3 frontend
├── soul-curator-backend/        # Spring Boot backend
├── soul-curator-ai/            # AI integration
└── documents/                   # Project documentation
```

## Development Guidelines

### Code Standards
- Backend: Follow Spring Boot best practices
- Frontend: Use Vue3 Composition API with TypeScript
- AI: Implement MCP protocol for tool integration
- Testing: 80%+ test coverage

### Git Workflow
- Main branch: `main` (protected)
- Feature branches: `feature/description`
- Commit messages: Conventional commits
- PR reviews: Required for all changes

### Quality Gates
- Code review before merge
- Automated tests must pass
- SonarQube quality gate
- Performance benchmarks

## Success Metrics

### Technical Metrics
- Page load time: < 2 seconds
- API response time: < 200ms
- Test coverage: > 80%
- Zero critical bugs at launch

### Product Metrics
- User engagement: Daily active users
- Recommendation accuracy: User satisfaction score
- Retention: 30-day retention rate
- Growth: Monthly user growth

## Risk Management

### Technical Risks
1. AI integration complexity
2. Performance bottlenecks
3. Third-party API dependencies
4. Data privacy compliance

### Mitigation Strategies
1. Prototype AI features early
2. Load testing throughout development
3. Fallback mechanisms for external APIs
4. Privacy-by-design approach

## Contact & Resources

### Project Owner
- 王达迅 (爹)
- Role: Full-stack developer & project lead

### AI Assistant
- 鞠大毛_mac版 (儿子)
- Role: Development assistant & memory keeper

### Key Files
- Manifesto: `soul-curator-manifesto.md`
- Architecture: `soul-curator-architecture.md`
- Progress: `soul-curator-progress-tracker.md`
- Memory: `MEMORY.md`