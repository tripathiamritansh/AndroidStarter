# Chat App Architecture

## 🏗️ Clean Architecture Pattern

This app follows **Clean Architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                             │
│  ┌──────────────┐         ┌─────────────┐                  │
│  │ ChatScreen   │ ────→   │ ChatViewModel│                  │
│  │ (Compose)    │   ←────│ (State)      │                  │
│  └──────────────┘         └──────┬──────┘                  │
└─────────────────────────────────────┼────────────────────────┘
                                     │ observe Flow<Messages>
                                     │
┌────────────────────────────────────┼────────────────────────┐
│                    Domain Layer    │                        │
│                  ┌─────────────────▼───────────┐            │
│                  │  ChatRepository (Interface) │            │
│                  └─────────────────────────────┘            │
└─────────────────────────────────────────────────────────────┘
                                     │
┌────────────────────────────────────┼────────────────────────┐
│                     Data Layer     │                        │
│  ┌─────────────────────────────────▼───────────┐            │
│  │         ChatRepositoryImpl                  │            │
│  │  (Business Logic - Coordinates API + DB)    │            │
│  └────┬───────────────────────────────┬────────┘            │
│       │                               │                     │
│  ┌────▼────────┐              ┌───────▼────────┐            │
│  │  OpenAI API │              │  Room Database │            │
│  │  (Remote)   │              │  (Local)       │            │
│  └─────────────┘              └────────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 Key Design Principles

### 1. **Single Source of Truth: Database**
- All data flows from the Room database
- ViewModel observes database via Flow
- UI automatically updates when database changes
- No manual state synchronization needed

### 2. **Repository Contains Business Logic**
The `ChatRepositoryImpl` orchestrates the entire flow:

```kotlin
suspend fun sendMessage(userMessage: String) {
    // 1. Save user message to DB
    messageDao.insertMessage(userMessage)
    
    // 2. Create streaming AI message in DB
    val aiMessage = Message(status = STREAMING)
    messageDao.insertMessage(aiMessage)
    
    // 3. Call API and stream response
    api.streamChatCompletion()
        .forEach { chunk ->
            // 4. Update DB with each chunk
            messageDao.updateMessageContent(aiMessageId, content)
        }
    
    // 5. Mark as complete in DB
    messageDao.updateStatus(aiMessageId, SENT)
}
```

### 3. **ViewModel is Thin**
The ViewModel only:
- ✅ Observes database Flow
- ✅ Handles user input (text field)
- ✅ Delegates actions to repository
- ❌ No business logic
- ❌ No direct API calls
- ❌ No manual state updates

```kotlin
fun onSendMessage() {
    val text = _inputText.value
    _inputText.value = "" // Clear input
    chatRepository.sendMessage(text) // Delegate to repository
    // That's it! Database updates automatically trigger UI refresh
}
```

---

## 📊 Data Flow Example

### User sends "Hello":

```
1. User types "Hello" → ChatScreen
   ↓
2. onSendMessage() called → ChatViewModel
   ↓
3. chatRepository.sendMessage("Hello") → ChatRepositoryImpl
   ↓
4. INSERT user message into DB → Room Database
   ↓
5. messageDao.getAllMessages() Flow emits → ViewModel receives
   ↓
6. UI recomposes with new message → ChatScreen shows "Hello"
   ↓
7. CREATE streaming AI message in DB → Room Database
   ↓
8. Flow emits again → UI shows typing indicator
   ↓
9. API call starts streaming → OpenAI API
   ↓
10. Each chunk received → UPDATE DB with new content
    ↓
11. Flow emits → UI updates in real-time
    ↓
12. Stream completes → UPDATE status to SENT in DB
    ↓
13. Flow emits final state → UI shows complete message
```

### Key insight:
**ViewModel never manually updates UI state!**
It all happens automatically through database Flow observation.

---

## 🗂️ File Structure

```
app/src/main/java/com/sample/starter/
│
├── domain/                          # Business models and contracts
│   ├── model/
│   │   └── Message.kt              # @Entity - data model
│   └── repository/
│       └── ChatRepository.kt       # Interface - what the ViewModel needs
│
├── data/                            # Implementation details
│   ├── local/
│   │   ├── ChatDatabase.kt         # Room database singleton
│   │   ├── MessageDao.kt           # Database queries
│   │   └── MessageConverters.kt    # Enum ↔ String conversion
│   ├── remote/
│   │   ├── OpenAiApi.kt            # Retrofit API interface
│   │   └── Dto.kt                  # API request/response models
│   └── repository/
│       └── ChatRepositoryImpl.kt   # Business logic implementation
│
└── ui/                              # Presentation layer
    └── chat/
        ├── ChatScreen.kt           # Compose UI
        ├── ChatViewModel.kt        # State holder
        └── ChatUiState.kt          # UI state model
```

---

## 🎓 Benefits of This Architecture

### 1. **Testability**
```kotlin
// Easy to test - inject mock repository
class ChatViewModelTest {
    @Test
    fun testSendMessage() {
        val mockRepo = MockChatRepository()
        val viewModel = ChatViewModel(mockRepo)
        
        viewModel.onSendMessage("Hello")
        
        verify(mockRepo).sendMessage("Hello")
    }
}
```

### 2. **Offline Support**
- Messages saved locally immediately
- Can view past conversations without internet
- Retry failed messages when connection restored

### 3. **Real-time Updates**
- Flow-based observation
- Automatic UI updates when database changes
- No manual state synchronization

### 4. **Scalability**
- Add pagination: just change DAO query
- Add search: just add DAO query
- Add filters: just change Flow mapping
- Repository handles complexity, ViewModel stays simple

### 5. **Separation of Concerns**
- **UI**: What to display
- **ViewModel**: When to display it
- **Repository**: How to get the data
- **Database**: Where data is stored
- **API**: Where data comes from

---

## 🔄 State Management

### UI State
```kotlin
data class ChatUiState(
    val messages: List<Message>,    // From database Flow
    val inputText: String,           // From ViewModel MutableStateFlow
    val screenState: ScreenState     // From ViewModel MutableStateFlow
)
```

### State Sources
1. **Database (via Flow)** → messages list
2. **ViewModel** → input text, error state
3. **Combined** → final UI state

### Why This Works
```kotlin
// ViewModel combines multiple sources
val uiState = combine(
    chatRepository.getAllMessages(), // From DB
    _inputText,                      // From ViewModel
    _errorState                      // From ViewModel
) { messages, input, error ->
    ChatUiState(messages, input, error)
}
```

---

## 🚀 Future Enhancements

### Easy to Add:

1. **Pagination**
   ```kotlin
   @Query("SELECT * FROM messages ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
   fun getMessagesPaginated(limit: Int, offset: Int): Flow<List<Message>>
   ```

2. **Search**
   ```kotlin
   @Query("SELECT * FROM messages WHERE content LIKE '%' || :query || '%'")
   fun searchMessages(query: String): Flow<List<Message>>
   ```

3. **Multiple Conversations**
   ```kotlin
   @Entity
   data class Message(
       val conversationId: String, // Add this field
       // ... other fields
   )
   
   @Query("SELECT * FROM messages WHERE conversationId = :id")
   fun getMessagesByConversation(id: String): Flow<List<Message>>
   ```

4. **Message Reactions**
   ```kotlin
   @Entity
   data class Message(
       val reactions: List<String>, // Add this field
       // ... other fields
   )
   ```

All without changing ViewModel or UI code!

---

## ✅ Architecture Checklist

- ✅ **Single Source of Truth**: Database
- ✅ **Unidirectional Data Flow**: DB → ViewModel → UI
- ✅ **Reactive**: Flow-based observation
- ✅ **Testable**: Repository interface, dependency injection
- ✅ **Offline-first**: Database persists data
- ✅ **Separation of Concerns**: Clear layer boundaries
- ✅ **Scalable**: Easy to add features
- ✅ **Production-ready**: Error handling, threading

---

## 🎯 Summary

**The Golden Rule:**
> ViewModel observes database, Repository updates database.
> Never manually update UI state - let the database Flow do it!

This architecture ensures:
- **Consistency**: UI always matches database
- **Simplicity**: ViewModel is thin and focused
- **Reliability**: Database is the source of truth
- **Maintainability**: Clear responsibilities per layer

