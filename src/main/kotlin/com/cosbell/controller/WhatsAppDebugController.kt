package com.cosbell.controller

import com.cosbell.service.WhatsAppService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/debug/whatsapp")
class WhatsAppDebugController(
    private val wa: WhatsAppService
) {
    data class Req(val to: String, val body: String)

    @PostMapping
    fun send(@RequestBody r: Req): ResponseEntity<Any> {
        val ok = wa.sendText(r.to, r.body)
        return if (ok) ResponseEntity.ok(mapOf("status" to "sent"))
        else ResponseEntity.status(500).body(mapOf("status" to "error"))
    }
}
