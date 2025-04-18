import User from "./user.model"
import Tour from "./tour.model"
import Application from "./application.model"
import Contact from "./contact.model"
import ClientTour from "./client-tour.model"

// Ассоциации пользователь-контакт (1:1)
User.hasOne(Contact, {
  foreignKey: "userId",
  as: "contact",
})
Contact.belongsTo(User, {
  foreignKey: "userId",
  as: "user",
})

// Ассоциации тур-заявка (1:M)
Tour.hasMany(Application, {
  foreignKey: "tourId",
  as: "applications",
})
Application.belongsTo(Tour, {
  foreignKey: "tourId",
  as: "tour",
})

// Ассоциации пользователь-заявка (1:M)
User.hasMany(Application, {
  foreignKey: "userId",
  as: "applications",
})
Application.belongsTo(User, {
  foreignKey: "userId",
  as: "user",
})

// Ассоциации контакт-тур через ClientTour (M:M)
Contact.belongsToMany(Tour, {
  through: ClientTour,
  foreignKey: "contactId",
  otherKey: "tourId",
  as: "tours",
})
Tour.belongsToMany(Contact, {
  through: ClientTour,
  foreignKey: "tourId",
  otherKey: "contactId",
  as: "contacts",
})

// Дополнительные ассоциации с моделью ClientTour
Contact.hasMany(ClientTour, {
  foreignKey: "contactId",
  as: "clientTours",
})
ClientTour.belongsTo(Contact, {
  foreignKey: "contactId",
  as: "contact",
})

Tour.hasMany(ClientTour, {
  foreignKey: "tourId",
  as: "clientTours",
})
ClientTour.belongsTo(Tour, {
  foreignKey: "tourId",
  as: "tour",
})

Application.hasOne(ClientTour, {
  foreignKey: "applicationId",
  as: "clientTour",
})
ClientTour.belongsTo(Application, {
  foreignKey: "applicationId",
  as: "application",
})

export { User, Tour, Application, Contact, ClientTour }
