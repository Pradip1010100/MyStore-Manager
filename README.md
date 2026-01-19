# \# 📱 MyStore Manager

# 

# \*\*MyStore Manager\*\* is an \*\*offline-first Android application\*\* built to manage \*\*sales, purchases, inventory, workers, accounting, and reports\*\* for small retail businesses — especially \*\*battery shops\*\*.

# 

# The app is designed to be \*\*simple, reliable, and practical\*\*, optimized for \*\*low-end Android devices\*\* and real-world shop workflows.

# 

# ---

# 

# \## 🎯 Project Objective

# 

# To provide a \*\*single Android app\*\* that allows a shop owner to:

# 

# \- Manage \*\*products and stock\*\*

# \- Handle \*\*battery sales with old battery exchange\*\*

# \- Record \*\*purchases and supplier dues\*\*

# \- Track \*\*worker salaries and advances\*\*

# \- Maintain \*\*accurate accounting\*\*

# \- View \*\*business reports\*\*

# \- Work \*\*100% offline\*\*

# 

# ---

# 

# \## ✨ Key Features

# 

# \### 👷 Worker Management

# \- Add, update, deactivate workers  

# \- Record salary \& advance payments  

# \- Worker ledger tracking  

# \- Automatic accounting entry for every payment  

# 

# \### 📦 Product \& Inventory Management

# \- Multiple product categories (Battery, UPS, Inverter, Accessories, Services)

# \- Add \& update products

# \- Real-time stock tracking

# \- Manual stock adjustment with reason \& audit trail

# \- Low-stock visibility

# 

# \### 🧾 Sales \& Billing

# \- Multi-product sales

# \- Old battery exchange (battery products only)

# \- Automatic bill calculation

# \- Cash / UPI / Credit payments

# \- Invoice generation \& sharing

# \- Accurate stock deduction

# 

# \### 🏭 Purchase \& Supplier Management

# \- Supplier management

# \- Cash, partial, and credit purchases

# \- Supplier due tracking

# \- Automatic stock increment

# \- Conditional accounting entry

# 

# \### 💰 Accounting \& Transactions

# \- Centralized transaction system (IN / OUT)

# \- Daily \& monthly summaries

# \- Profit / loss foundation

# \- No missing or duplicate entries

# 

# \### 📊 Reports

# \- Sales reports

# \- Stock reports

# \- Worker payment reports

# \- Purchase reports

# \- Old battery collection reports

# 

# ---

# 

# \## 🛠️ Tech Stack

# 

# | Layer | Technology |

# |-----|-----------|

# | Language | Kotlin |

# | UI | Jetpack Compose |

# | Architecture | MVVM |

# | Database | Room (SQLite) |

# | State | ViewModel + StateFlow |

# | Persistence | Offline-first |

# | Build Tool | Gradle |

# | Optional (Future) | Firebase / Cloud Backup |

# 

# ---

# 

# \## 🧱 Architecture Overview

# 

# MyStore Manager strictly follows \*\*Clean MVVM Architecture\*\*:

# 

# UI (Compose Screens)  

# ↓  

# ViewModel (State \& Validation)  

# ↓  

# Repository (Business Logic)  

# ↓  

# DAO (Room)  

# ↓  

# SQLite Database  

# 

# \### Architecture Rules

# \- UI never accesses the database directly

# \- ViewModel never talks to DAO

# \- Repository owns all business logic

# \- All money movements go through `Transaction`

# 

# ---

# 

# \## 🗂️ Core Modules

# 

# \- Sales \& Billing

# \- Purchases \& Suppliers

# \- Inventory \& Stock

# \- Worker Management

# \- Accounting

# \- Reports

# 

# ---

# 

# \## 🗄️ Database Design Highlights

# 

# \- Fully normalized schema

# \- Central `Transaction` entity for all financial events

# \- Stock stored in a single authoritative table

# \- No hard deletes (audit-safe)

# \- Append-only accounting records

# 

# ---

# 

# \## 🔁 Development Process

# 

# \- \*\*Methodology:\*\* Scrum (Lightweight)

# \- \*\*Sprint Length:\*\* 2 weeks

# \- \*\*Delivery:\*\* Incremental working APK

# 

# \### Definition of Done

# \- Works offline

# \- Data persists after restart

# \- No crash on low-end devices

# \- Verified on real Android phone

# 

# ---

# 

# \## 📦 Sprint Overview

# 

# 1\. Sprint 0 – Requirements, UML, Architecture  

# 2\. Sprint 1 – App setup, Room DB, Workers \& Products  

# 3\. Sprint 2 – Sales \& Billing  

# 4\. Sprint 3 – Purchases \& Inventory  

# 5\. Sprint 4 – Worker Payments \& Accounting  

# 6\. Sprint 5 – Reports  

# 7\. Sprint 6 – Polish \& Release  

# 

# ---

# 

# \## 📱 Offline-First Design

# 

# \- Fully functional without internet

# \- Local Room database

# \- No server dependency

# \- Optional cloud backup planned

# 

# ---

# 

# \## 🔐 Data Integrity Rules

# 

# \- Stock never goes negative

# \- Transactions are immutable

# \- Corrections use reversal entries

# \- Accounting is the single source of truth

# \- No silent data manipulation

# 

# ---

# 

# \## 🧪 Testing Strategy

# 

# \- Manual testing on real devices

# \- Focus on:

# &nbsp; - Stock accuracy

# &nbsp; - Accounting correctness

# &nbsp; - Crash-free operation

# &nbsp; - Real business scenarios

# 

# ---

# 

# \## 📄 Documentation

# 

# \- UML Diagrams (Use Case, ER, Class, Sequence, Statechart, Component, Deployment)

# \- Database schema

# \- Business rules

# \- Sprint plan

# \- Workflow definitions

# 

# ---

# 

# \## 📦 Deliverables

# 

# \- Android source code (Kotlin)

# \- APK / AAB

# \- Database design

# \- UML diagrams

# \- User documentation

# \- Release notes

# 

# ---

# 

# \## 🔮 Future Enhancements

# 

# \- Cloud backup \& restore

# \- Multi-device sync

# \- User authentication

# \- GST / tax support

# \- Barcode scanning

# \- Advanced reporting

# 

# ---

# 

# \## 👤 Target Users

# 

# \- Small retail shop owners

# \- Battery shops

# \- Electrical / hardware stores

# \- Offline-first business users

# 

# ---

# 

# \## 📜 License

# 

# This project is \*\*private / client-owned\*\* unless stated otherwise.

# 

# ---

# 

# \## ✅ Project Status

# 

# \- Architecture \& UML: \*\*Completed\*\*

# \- Development: \*\*In Progress\*\*

# \- Production Ready Target: \*\*Sprint 6\*\*

# 

# ---

# 

# \*\*MyStore Manager – Built for real shops. Simple. Offline. Reliable.\*\*

