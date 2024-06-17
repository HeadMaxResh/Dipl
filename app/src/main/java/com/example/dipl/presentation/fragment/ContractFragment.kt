package com.example.dipl.presentation.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.dipl.databinding.FragmentContractBinding
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfVersion
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream


class ContractFragment : Fragment() {

    private var _binding: FragmentContractBinding? = null
    private val binding: FragmentContractBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentContractBinding == null")

    private val args: ContractFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContractBinding.inflate(inflater, container, false)

        binding.city.text = args.contract.apartmentInfo.city.toString()
        binding.date.text = args.contract.date
        binding.fioOwner.text =
            "${args.contract.passportOwner.lastname}" +
                    " ${args.contract.passportOwner.name} " +
                    "${args.contract.passportOwner.surname} "
        binding.passportOwner.text = args.contract.passportOwner.toString()
        binding.fioUser.text =
            "${args.contract.passportSender.lastname}" +
                    " ${args.contract.passportSender.name} " +
                    "${args.contract.passportSender.surname} "
        binding.passportUser.text = args.contract.passportSender.toString()
        binding.apartmentAddress.text = args.contract.apartmentInfo.name
        binding.rent.text = args.contract.apartmentInfo.rent.toString()
        binding.fioOwnerEnd.text =
            "${args.contract.passportOwner.lastname}" +
                    " ${args.contract.passportOwner.name} " +
                    "${args.contract.passportOwner.surname} "
        binding.signOwner.text = args.contract.ownerElectronicSignature
        binding.fioUserEnd.text =
            "${args.contract.passportSender.lastname}" +
                    " ${args.contract.passportSender.name} " +
                    "${args.contract.passportSender.surname} "
        binding.signUser.text = args.contract.senderElectronicSignature

        binding.btnSavePdf.setOnClickListener {
            createPdfDocumentInDownloads()
            Toast.makeText(
                activity,
                "PDF документ успешно сохранен в директории Downloads.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    private val WRITE_EXTERNAL_STORAGE_REQUEST = 101

    private fun createPdfDocumentInDownloads() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST
            )
        } else {
            savePdfToDownloadsDirectory()
        }
    }

    private fun savePdfToDownloadsDirectory() {
        val fileName = "example2.pdf"

        val downloadsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val pdfFile = File(downloadsDirectory, fileName)

        val fos = FileOutputStream(pdfFile)
        val writer = PdfWriter(
            fos,
            WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)
                .setFullCompressionMode(true)
        )
        val pdf = PdfDocument(writer)
        val document = Document(pdf)


        val textViewList = listOf(
            binding.tvTitle,
            binding.city,
            binding.date,
            binding.tv1,
            binding.fioOwner,
            binding.tv2,
            binding.passportOwner,
            binding.tv3,
            binding.tv4,
            binding.fioUser,
            binding.tv5,
            binding.passportUser,
            binding.tv6,
            binding.tv7,
            binding.tv8,
            binding.apartmentAddress,
            binding.tv9,
            binding.tv10,
            binding.tv11,
            binding.tv12,
            binding.tv13,
            binding.tv14,
            binding.tv15,
            binding.rent,
            binding.tv16,
            binding.tv17,
            binding.tv18,
            binding.tv19,
            binding.tv20,
            binding.tv21,
            binding.tv22,
            binding.tv23,
            binding.tv24,
            binding.fioOwnerEnd,
            binding.tv25,
            binding.signOwner,
            binding.tv26,
            binding.fioUserEnd,
            binding.tv27,
            binding.signUser,
        )
        val font: PdfFont = PdfFontFactory.createFont("Helvetica", PdfEncodings.UTF8, true)

        textViewList.forEach { textView ->
            val text = textView.text.toString()


            val paragraph = Paragraph(text).setFont(font)

            document.add(paragraph)
        }

        //document.add(Paragraph(fileContent))

        document.close()

        println("PDF документ успешно создан и сохранен в директории Downloads.")
    }
}