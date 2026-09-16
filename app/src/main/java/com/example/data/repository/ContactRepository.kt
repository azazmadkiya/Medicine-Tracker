package com.example.data.repository

import com.example.data.local.dao.ContactDao
import com.example.data.local.entity.Contact
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {
    val pharmacies: Flow<List<Contact>> = contactDao.getContactsByType("PHARMACY")
    val hospitals: Flow<List<Contact>> = contactDao.getContactsByType("HOSPITAL")
    val professionals: Flow<List<Contact>> = contactDao.getContactsByType("DOCTOR")

    suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }
}
